package com.nanaios.applied_ammo_box.mixin.aeinfinitybooster;

import appeng.blockentity.networking.WirelessAccessPointBlockEntity;
import com.nanaios.applied_ammo_box.util.AE2LinkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.hexeption.aeinfinitybooster.setup.ModItems;

@Mixin(value = AE2LinkHelper.class, remap = false)
public class MixinAE2LinkHelper {
    @Inject(method = "getWapSqDistance", at = @At(value = "INVOKE", target = "appeng/blockentity/networking/WirelessAccessPointBlockEntity.getRange()D"), cancellable = true)
    private static void applied_ammo_box$mixinGetWapSqDistance$infinityCardInject(WirelessAccessPointBlockEntity wap, BlockPos pos, Level level, CallbackInfoReturnable<Double> cir) {
        // Infinityカード装着時は距離を問わず256mに設定
        if (wap.getInternalInventory().getStackInSlot(0).is(ModItems.INFINITY_CARD.get())) {
            cir.setReturnValue(256.0D);
        }
    }

    @Inject(method = "getWapSqDistance", at = @At(value = "INVOKE", target = "appeng/blockentity/networking/WirelessAccessPointBlockEntity.getLocation()Lappeng/api/util/DimensionalBlockPos;"), cancellable = true)
    private static void applied_ammo_box$mixinGetWapSqDistance$dimensionCardInject(WirelessAccessPointBlockEntity wap, BlockPos pos, Level level, CallbackInfoReturnable<Double> cir) {
        // Dimensionカード装着時はディメンションを問わず1024mに設定
        if (wap.getInternalInventory().getStackInSlot(0).is(ModItems.DIMENSION_CARD.get())) {
            cir.setReturnValue(1024.0);
        }
    }
}
