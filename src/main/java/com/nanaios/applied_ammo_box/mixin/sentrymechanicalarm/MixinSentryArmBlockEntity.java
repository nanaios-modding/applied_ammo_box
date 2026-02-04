package com.nanaios.applied_ammo_box.mixin.sentrymechanicalarm;

import com.llamalad7.mixinextras.sugar.Local;
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
public abstract class MixinSentryArmBlockEntity {
    @Shadow
    @Final
    public NonNullList<ItemStack> attachedAmmoBoxes;

    @Shadow
    public abstract ItemStack getHeldItem();

    @Inject(method = "addAmmoBox",at = @At(value = "INVOKE", target = "net/minecraft/core/NonNullList.set(ILjava/lang/Object;)Ljava/lang/Object;"))
    private void applied_ammo_box$addAmmoBox(ItemStack ignore, CallbackInfoReturnable<Boolean> cir, @Local(name = "copy") ItemStack copy) {
        if(copy.getItem() instanceof WirelessAmmoBoxItem wirelessAmmoBoxItem) {
            // ワイヤレス弾薬箱の場合、設置された位置情報を保存する
            BlockEntity blockEntity = (SentryArmBlockEntity)(Object)this;
            Level level = blockEntity.getLevel();
            if(level == null || level.isClientSide) return;
            BlockPos pos = blockEntity.getBlockPos();
            wirelessAmmoBoxItem.setLevel(copy,level);
            wirelessAmmoBoxItem.setPos(copy, pos);

            // 手持ちの銃を取得する
            ItemStack gunStack = this.getHeldItem();
            wirelessAmmoBoxItem.updateAmmoId(copy, gunStack);
        }
    }

    @Inject(method = "tick",at = @At("HEAD"))
    private void applied_ammo_box$tick(CallbackInfo ci) {
        Level level = ((SentryArmBlockEntity)(Object)this).getLevel();
        if(level == null) return;
        SentryMechanicalArmUtil.updateAttachedAmmoBoxes(level, attachedAmmoBoxes);
    }
}
