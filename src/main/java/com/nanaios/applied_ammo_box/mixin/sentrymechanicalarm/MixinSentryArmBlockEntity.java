package com.nanaios.applied_ammo_box.mixin.sentrymechanicalarm;

import com.nanaios.applied_ammo_box.item.WirelessAmmoBoxItem;
import euphy.upo.sentrymechanicalarm.content.SentryArmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SentryArmBlockEntity.class,remap = false)
public class MixinSentryArmBlockEntity {
    @Shadow
    @Final
    public NonNullList<ItemStack> attachedAmmoBoxes;

    @Inject(method = "addAmmoBox",at = @At(value = "INVOKE", target = "net/minecraft/core/NonNullList.set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private void applied_ammo_box$addAmmoBox(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if(stack.getItem() instanceof WirelessAmmoBoxItem wirelessAmmoBoxItem) {
            BlockEntity blockEntity = (BlockEntity)(Object)this;
            Level level = blockEntity.getLevel();
            BlockPos pos = blockEntity.getBlockPos();
            if(level == null) return;
            wirelessAmmoBoxItem.setLevel(stack,level);
            wirelessAmmoBoxItem.setPos(stack, pos);
        }
    }

    @Inject(method = "tick",at = @At("HEAD"))
    private void applied_ammo_box$tick(CallbackInfo ci) {
        Level level = ((BlockEntity)(Object)this).getLevel();

        // サーバーサイドでのみ処理を行う
        if(level != null && level.isClientSide) return;

        for(ItemStack itemStack : this.attachedAmmoBoxes) {
            if(itemStack.isEmpty()) continue;
            if(!(itemStack.getItem() instanceof WirelessAmmoBoxItem wirelessAmmoBoxItem)) continue;
            if(!wirelessAmmoBoxItem.isWantUpdate(itemStack)) continue;

            wirelessAmmoBoxItem.updateAmmoCount(itemStack);
        }
    }
}
