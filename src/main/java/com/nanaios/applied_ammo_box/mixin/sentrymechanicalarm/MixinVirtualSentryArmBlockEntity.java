package com.nanaios.applied_ammo_box.mixin.sentrymechanicalarm;

import com.nanaios.applied_ammo_box.item.WirelessAmmoBoxItem;
import euphy.upo.sentrymechanicalarm.content.VirtualSentryArmBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VirtualSentryArmBlockEntity.class,remap = false)
public class MixinVirtualSentryArmBlockEntity {
    @Inject(method = "setVirtualLevel",at = @At("HEAD"))
    private void applied_ammo_box$setVirtualLevel(Level level, CallbackInfo ci) {
        NonNullList<ItemStack> attachedAmmoBoxes = ((VirtualSentryArmBlockEntity)(Object)this).attachedAmmoBoxes;
        for(ItemStack stack: attachedAmmoBoxes) {
            if(stack.getItem() instanceof WirelessAmmoBoxItem wirelessAmmoBoxItem) {
                wirelessAmmoBoxItem.setLevel(stack, level);
            }
        }
    }

    @Inject(method = "setVirtualPos",at = @At("HEAD"))
    private void applied_ammo_box$setVirtualPos(BlockPos pos, CallbackInfo ci) {
        NonNullList<ItemStack> attachedAmmoBoxes = ((VirtualSentryArmBlockEntity)(Object)this).attachedAmmoBoxes;
        for(ItemStack stack: attachedAmmoBoxes) {
            if(stack.getItem() instanceof WirelessAmmoBoxItem wirelessAmmoBoxItem) {
                wirelessAmmoBoxItem.setPos(stack, pos);
            }
        }
    }
}
