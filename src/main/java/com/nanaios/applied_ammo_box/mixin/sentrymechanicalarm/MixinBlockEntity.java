package com.nanaios.applied_ammo_box.mixin.sentrymechanicalarm;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BlockEntity.class)
public interface MixinBlockEntity {
    @Accessor(value = "level")
    Level applied_ammo_box$getLevel();
}
