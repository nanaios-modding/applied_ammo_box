package com.nanaios.applied_ammo_box.item;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.core.localization.PlayerMessages;
import com.nanaios.applied_ammo_box.util.AE2LinkHelper;
import com.nanaios.applied_ammo_box.util.AE2LinkHelper.ActionResult;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class WirelessAmmoBoxItem extends AmmoBoxItem implements IAEItemPowerStorage,ITimeStamp {
    public ResourceLocation ammoId;
    public WirelessAmmoBoxItem() {
        super();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        // サーバーサイドでのみ動作させるためにチェック
        if(level.isClientSide()) return;

        // 負荷軽減のため1秒に1回更新する
        if((System.currentTimeMillis() - getTimeStamp(stack)) > 1000) {
            // タイムスタンプを更新
            setTimeStamp(stack, System.currentTimeMillis());

            ItemStack ammo = AmmoItemBuilder.create().setId(ammoId).setCount(1).build();
            GlobalPos pos = GlobalPos.of(level.dimension(), entity.blockPosition());

            // 弾薬数を更新
            ActionResult.Wrapper result = AE2LinkHelper.extractionAmmo(pos, stack, ammo, Integer.MAX_VALUE, Actionable.SIMULATE);

            if(result.action() == ActionResult.SUCCESS) {
                AE2LinkHelper.setLinked(stack,true);
                return;
            }

            AE2LinkHelper.setLinked(stack,false);
            if(entity instanceof Player player) {
                switch (result.action()) {
                    case DEVICE_NOT_LINKED -> player.displayClientMessage(PlayerMessages.DeviceNotLinked.text(), true);
                    case LINKED_NETWORK_NOT_FOUND -> player.displayClientMessage(PlayerMessages.LinkedNetworkNotFound.text(), true);
                }
            }
        }
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

    @Override
    public int getBarWidth(ItemStack stack) {
        double filled = getAECurrentPower(stack) / getAEMaxPower(stack);
        return Mth.clamp((int) (filled * 13), 0, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // This is the standard green color of full durability bars
        return Mth.hsvToRgb(1 / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public double injectAEPower(ItemStack stack, double amount, Actionable mode) {
        return 0;
    }

    @Override
    public double extractAEPower(ItemStack stack, double amount, Actionable mode) {
        return 0;
    }

    @Override
    public double getAEMaxPower(ItemStack stack) {
        return 0;
    }

    @Override
    public double getAECurrentPower(ItemStack stack) {
        return 0;
    }

    @Override
    public AccessRestriction getPowerFlow(ItemStack stack) {
        return AccessRestriction.WRITE;
    }

    @Override
    public double getChargeRate(ItemStack stack) {
        return 0;
    }
}
