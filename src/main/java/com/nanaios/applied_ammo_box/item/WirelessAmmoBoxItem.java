package com.nanaios.applied_ammo_box.item;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.implementations.blockentities.IWirelessAccessPoint;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import appeng.core.localization.PlayerMessages;
import appeng.me.helpers.BaseActionSource;
import appeng.me.helpers.ChannelPowerSrc;
import com.nanaios.applied_ammo_box.util.AE2LinkHelper;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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

            // 弾薬数を更新
            UpdateResult result = updateAmmoCount(stack, GlobalPos.of(level.dimension(), entity.blockPosition()));

            if(result == UpdateResult.SUCCESS) {
                AE2LinkHelper.setLinked(stack,true);
                return;
            }

            AE2LinkHelper.setLinked(stack,false);
            if(entity instanceof Player player) {
                switch (result) {
                    case DEVICE_NOT_LINKED -> player.displayClientMessage(PlayerMessages.DeviceNotLinked.text(), true);
                    case LINKED_NETWORK_NOT_FOUND -> player.displayClientMessage(PlayerMessages.LinkedNetworkNotFound.text(), true);
                }
            }
        }
    }

    /// 弾薬の数をAE2ネットワークから取得し更新する \
    /// サーバーサイドでのみ動作
    /// @param itemStack 弾薬箱のItemStack
    /// @param pos 弾薬箱の座標
    @OnlyIn(Dist.DEDICATED_SERVER)
    public UpdateResult updateAmmoCount(ItemStack itemStack,GlobalPos pos) {
        // 座標を取得
        GlobalPos linkPos = AE2LinkHelper.getLinkedPosition(itemStack);
        if (linkPos == null) return UpdateResult.DEVICE_NOT_LINKED;

        // グリッドを取得
        IGrid grid = AE2LinkHelper.getGrid(linkPos);
        if (grid == null) return UpdateResult.LINKED_NETWORK_NOT_FOUND;

        // 有効範囲内のアクセスポイントを取得
        IWirelessAccessPoint wap = AE2LinkHelper.getBestWap(grid,pos);
        if (wap == null) return UpdateResult.LINKED_NETWORK_NOT_FOUND;

        // グリッドノードを取得
        IGridNode node = wap.getActionableNode();
        if(node == null) return UpdateResult.LINKED_NETWORK_NOT_FOUND;

        // 弾薬のデータを生成
        IActionSource source = new BaseActionSource();
        ItemStack ammoStack = AmmoItemBuilder.create().setId(ammoId).setCount(1).build();
        AEKey key = AEItemKey.of(ammoStack);
        if(key == null) return UpdateResult.LINKED_NETWORK_NOT_FOUND;

        // 弾薬の数を取得
        int ammoCount = (int) StorageHelper.poweredExtraction(new ChannelPowerSrc(node, grid.getEnergyService()), grid.getStorageService().getInventory(), key, Long.MAX_VALUE, source, Actionable.SIMULATE);
        // 弾薬数を0以上に補正
        ammoCount = Math.max(ammoCount,0);

        // 弾薬の数を更新
        super.setAmmoCount(itemStack, ammoCount);

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


    public enum UpdateResult {
        SUCCESS,
        DEVICE_NOT_LINKED,
        LINKED_NETWORK_NOT_FOUND
    }
}
