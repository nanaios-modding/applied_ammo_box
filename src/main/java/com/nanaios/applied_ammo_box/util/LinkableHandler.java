package com.nanaios.applied_ammo_box.util;

import appeng.api.features.IGridLinkableHandler;
import appeng.api.ids.AEComponents;
import com.nanaios.applied_ammo_box.item.ILinkableItem;
import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxDataComponents;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;

public class LinkableHandler implements IGridLinkableHandler {
    @Override
    public boolean canLink(ItemStack stack) {
        return stack.getItem() instanceof ILinkableItem;
    }

    @Override
    public void link(ItemStack itemStack, GlobalPos pos) {
        itemStack.set(AEComponents.WIRELESS_LINK_TARGET, pos);
        if (itemStack.getItem() instanceof ILinkableItem) {
            itemStack.set(AppliedAmmoBoxDataComponents.AMMO_BOX_LINKED, true);
        }
    }

    @Override
    public void unlink(ItemStack itemStack) {
        itemStack.remove(AEComponents.WIRELESS_LINK_TARGET);
        itemStack.remove(AppliedAmmoBoxDataComponents.AMMO_BOX_LINKED);
    }
}