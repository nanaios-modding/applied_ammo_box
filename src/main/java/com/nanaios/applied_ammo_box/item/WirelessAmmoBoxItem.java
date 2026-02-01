package com.nanaios.applied_ammo_box.item;

import appeng.api.config.Actionable;
import appeng.api.features.IGridLinkableHandler;
import appeng.core.localization.PlayerMessages;
import com.nanaios.applied_ammo_box.util.AE2LinkHelper;
import com.nanaios.applied_ammo_box.util.AE2LinkHelper.ActionResult;
import com.nanaios.applied_ammo_box.util.LinkableHandler;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

public class WirelessAmmoBoxItem extends AmmoBoxItem implements IDefaultAEItemPowerStorage,ITimeStamp,ILinkableItem {
    public static IGridLinkableHandler LINKABLE_HANDLER = new LinkableHandler();

    public GlobalPos pos;

    public WirelessAmmoBoxItem() {

    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        // サーバーサイドでのみ動作させる
        ServerLifecycleHooks.getCurrentServer().execute(() -> {
            // プレイヤーでなければ処理を中断
            if (!(entity instanceof Player player)) return;

            // 弾薬箱の座標を取得
            pos = GlobalPos.of(level.dimension(), entity.blockPosition());
            // 弾薬のIDを取得
            ItemStack iGunStack = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (iGunStack.getItem() instanceof IGun gun) {
                ResourceLocation gunId = gun.getGunId(iGunStack);
                setAmmoId(
                        stack,
                        TimelessAPI.getCommonGunIndex(gunId)
                                .map(commonGunIndex -> commonGunIndex.getGunData().getAmmoId())
                                .orElse(DefaultAssets.EMPTY_AMMO_ID)
                );
            }

            // 負荷軽減のため1秒に1回更新する
            if ((System.currentTimeMillis() - getTimeStamp(stack)) > 1000) {
                // タイムスタンプを更新
                setTimeStamp(stack, System.currentTimeMillis());

                // 弾薬数を更新
                ActionResult result = updateAmmoCount(stack);

                switch (result.status()) {
                    case DEVICE_NOT_LINKED -> player.displayClientMessage(PlayerMessages.DeviceNotLinked.text(), true);
                    case LINKED_NETWORK_NOT_FOUND ->
                            player.displayClientMessage(PlayerMessages.LinkedNetworkNotFound.text(), true);
                }
            }
        });
    }

    /// 弾薬数をAE2ネットワークから取得し更新する
    /// @param stack 弾薬箱のItemStack
    public ActionResult updateAmmoCount(ItemStack stack) {
        // 弾薬の情報を取得
        ItemStack ammo = AmmoItemBuilder.create().setId(getAmmoId(stack)).setCount(1).build();
        // 弾薬数を更新
        ActionResult result = AE2LinkHelper.extractionAmmo(pos, stack, ammo, Integer.MAX_VALUE, Actionable.SIMULATE);
        // 弾薬箱の弾薬数を直接更新
        super.setAmmoCount(stack,result.count());
        // リンク状態を更新
        this.setLinked(stack,result.status() == ActionResult.Status.SUCCESS);

        return result;
    }

    @Override
    public void setAmmoCount(ItemStack ammoBox, int count) {
        int oldCount = this.getAmmoCount(ammoBox);
        //弾薬が減少している個数を計算
        int diff = oldCount - count;

        if(diff <= 0) return;

        // 弾薬をAE2ネットワークから取り出す
        ItemStack ammo = AmmoItemBuilder.create().setId(getAmmoId(ammoBox)).setCount(1).build();
        AE2LinkHelper.extractionAmmo(pos, ammoBox, ammo, diff, Actionable.MODULATE);

        // 弾薬数を再取得して設定
        updateAmmoCount(ammoBox);
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
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || !ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public IGridLinkableHandler getLinkableHandler() {
        return LINKABLE_HANDLER;
    }
}
