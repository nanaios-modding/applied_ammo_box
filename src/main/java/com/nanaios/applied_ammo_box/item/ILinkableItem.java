package com.nanaios.applied_ammo_box.item;

import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxDataComponents;
import net.minecraft.world.item.ItemStack;

public interface ILinkableItem {

    default boolean isLinked(ItemStack stack) {
        return stack.getOrDefault(AppliedAmmoBoxDataComponents.AMMO_BOX_LINKED, false);
    }

}