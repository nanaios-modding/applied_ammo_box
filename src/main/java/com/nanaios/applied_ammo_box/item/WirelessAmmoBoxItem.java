package com.nanaios.applied_ammo_box.item;

import appeng.api.networking.IGrid;
import com.nanaios.applied_ammo_box.util.AE2LinkHelper;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class WirelessAmmoBoxItem extends AmmoBoxItem {
    public WirelessAmmoBoxItem() {
        super();
    }

    public UpdateResult updateAmmoCount(ItemStack itemStack) {
        // 座標を取得
        GlobalPos linkPos = AE2LinkHelper.getLinkedPosition(itemStack);
        if (linkPos == null) return UpdateResult.DEVICE_NOT_LINKED;

        // グリッドを取得
        IGrid grid = AE2LinkHelper.getGrid(linkPos);
        if (grid == null) return UpdateResult.LINKED_NETWORK_NOT_FOUND;

        return UpdateResult.SUCCESS;
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return Component.translatable("item.applied_ammo_box.ammo_box");
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack pOther, Slot slot, ClickAction action, Player player, SlotAccess access) {
        return false;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack ammoBox, Slot slot, ClickAction action, Player player) {
        return false;
    }

    public enum UpdateResult {
        SUCCESS,
        DEVICE_NOT_LINKED,
        LINKED_NETWORK_NOT_FOUND
    }
}
