package com.nanaios.applied_ammo_box.item;

import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxDataComponents;
import net.minecraft.world.item.ItemStack;

/// Interface for items with timestamps
public interface ITimeStamp {

    default long getTimeStamp(ItemStack stack) {
        return stack.getOrDefault(AppliedAmmoBoxDataComponents.AMMO_BOX_REFRESH, 0L);
    }

    default void setTimeStamp(ItemStack stack, long value) {
        stack.set(AppliedAmmoBoxDataComponents.AMMO_BOX_REFRESH, value);
    }
}
