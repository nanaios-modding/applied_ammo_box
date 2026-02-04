package com.nanaios.applied_ammo_box.mixin.sentrymechanicalarm;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import euphy.upo.sentrymechanicalarm.content.SentryMovementBehaviour;
import euphy.upo.sentrymechanicalarm.content.VirtualSentryArmBlockEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SentryMovementBehaviour.class,remap = false)
public class MixinSentryMovementBehaviour {
    @Inject(method = "tick",at = @At(value = "INVOKE", target = "euphy/upo/sentrymechanicalarm/content/SentryMovementBehaviour.tickServerLogic(Lcom/simibubi/create/content/contraptions/behaviour/MovementContext;)V"))
    private void applied_ammo_box$tick(MovementContext context, CallbackInfo ci, @Local(name = "virtualBE") VirtualSentryArmBlockEntity sentryArmBlockEntity) {
        Level level = sentryArmBlockEntity.getLevel();
        if(level == null) return;

        SentryMechanicalArmUtil.updateAttachedAmmoBoxes(level, sentryArmBlockEntity.attachedAmmoBoxes);
    }
}
