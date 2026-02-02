package com.nanaios.applied_ammo_box.mixin.aeinfinitybooster;

import appeng.blockentity.networking.WirelessAccessPointBlockEntity;
import com.nanaios.applied_ammo_box.util.AE2LinkHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import uk.co.hexeption.aeinfinitybooster.setup.ModItems;

@Mixin(value = AE2LinkHelper.class,remap = false)
public class MixinAE2LinkHelper {
    @Inject(method = "getWapSqDistance",at =@At("HEAD"), cancellable = true)
    private static void applied_ammo_box$mixinGetWapSqDistance(WirelessAccessPointBlockEntity wap, BlockPos pos, ServerLevel level, CallbackInfoReturnable<Double> cir) {
        // Dimensionカード装着時はディメンションを問わず1024mに設定
        if (wap.getInternalInventory().getStackInSlot(0).is(ModItems.DIMENSION_CARD.get())) {
            cir.setReturnValue(1024.0D);
        }

        //  Dimensionカード装着時以外は次元が違う場合は処理しない
        if(wap.getLocation().getLevel() != level) {
            return;
        }

        // Infinityカード装着時は距離を問わず256mに設定
        if (wap.getInternalInventory().getStackInSlot(0).is(ModItems.INFINITY_CARD.get())) {
            cir.setReturnValue(256.0D);
        }
    }
}
