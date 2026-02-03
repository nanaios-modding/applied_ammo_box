package com.nanaios.applied_ammo_box.item;

import appeng.api.config.Actionable;
import appeng.core.localization.GuiText;
import appeng.core.localization.PlayerMessages;
import appeng.core.localization.Tooltips;
import com.nanaios.applied_ammo_box.capabilitys.WirelessAmmoBoxCapabilityProvider;
import com.nanaios.applied_ammo_box.config.AppliedAmmoBoxConfig;
import com.nanaios.applied_ammo_box.util.AE2LinkHelper;
import com.nanaios.applied_ammo_box.util.AE2LinkHelper.ActionResult;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WirelessAmmoBoxItem extends AmmoBoxItem implements IDefaultAEItemPowerStorage, ITimeStamp, ILinkableItem {
    public static String NBT_LEVEL_KEY = "ammoBoxExistLevel";
    public static String NBT_BLOCK_POS_KEY = "ammoBoxExistBlockPos";

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        // サーバーサイドでのみ動作させる
        if (level.isClientSide()) return;

        // プレイヤーでなければ処理を中断
        if (!(entity instanceof Player player)) return;

        // 弾薬のIDを取得
        ItemStack iGunStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        boolean isUpdate = updateAmmoId(stack, iGunStack);

        // 負荷軽減のため1秒に1回更新する
        if (isWantUpdate(stack) || isUpdate) {
            // 弾薬箱の座標を取得
            setPos(stack, player.blockPosition());
            setLevel(stack, level);

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
    }

    /// 弾薬箱が存在するレベルを設定する
    ///
    /// @param stack 弾薬箱のItemStack
    /// @param level レベル
    public void setLevel(ItemStack stack,Level level) {
        stack.getOrCreateTag().putString(NBT_LEVEL_KEY,level.dimension().location().toString());
    }

    /// 弾薬箱が存在するレベルを取得する
    ///
    /// @param stack 弾薬箱のItemStack
    @Nullable
    public Level getLevel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_LEVEL_KEY)) {
            return ServerLifecycleHooks.getCurrentServer().getLevel(
                    ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(tag.getString(NBT_LEVEL_KEY)))
            );
        }
        return null;
    }

    /// 弾薬箱が存在する座標を設定する
    ///
    /// @param stack 弾薬箱のItemStack
    /// @param pos   座標
    public void setPos(ItemStack stack, BlockPos pos) {
        stack.getOrCreateTag().putLong(NBT_BLOCK_POS_KEY, pos.asLong());
    }

    /// 弾薬箱が存在する座標を取得する
    ///
    /// @param stack 弾薬箱のItemStack
    @Nullable
    public BlockPos getPos(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(NBT_BLOCK_POS_KEY)) {
            return BlockPos.of(tag.getLong(NBT_BLOCK_POS_KEY));
        }
        return null;
    }

    /// 弾薬箱の情報を更新する必要があるかどうかを返す
    ///
    /// @param stack 弾薬箱のItemStack
    public boolean isWantUpdate(ItemStack stack) {
        return (System.currentTimeMillis() - getTimeStamp(stack)) > 1000;
    }

    /// 弾薬IDを銃から取得し更新する
    /// 更新が発生した場合はtrueを返す
    ///
    /// @param ammoBox  弾薬箱のItemStack
    /// @param gunStack 銃のItemStack
    public boolean updateAmmoId(ItemStack ammoBox, ItemStack gunStack) {
        // 銃でなければ処理を中断
        if (!(gunStack.getItem() instanceof IGun gun)) return false;

        // 弾薬のIDを取得
        ResourceLocation ammoId = TimelessAPI.getCommonGunIndex(gun.getGunId(gunStack))
                .map(commonGunIndex -> commonGunIndex.getGunData().getAmmoId())
                .orElse(DefaultAssets.EMPTY_AMMO_ID);

        // 弾薬IDが同じなら処理を中断
        if (ammoId.equals(getAmmoId(ammoBox))) return false;

        // 弾薬IDを更新
        setAmmoId(ammoBox, ammoId);
        return true;
    }

    /// 弾薬数をAE2ネットワークから取得し更新する
    ///
    /// @param stack 弾薬箱のItemStack
    public ActionResult updateAmmoCount(ItemStack stack) {
        // 弾薬の情報を取得
        ItemStack ammo = AmmoItemBuilder.create().setId(getAmmoId(stack)).setCount(1).build();

        // 弾薬箱が存在するレベルと座標を取得
        BlockPos pos = getPos(stack);
        Level level = getLevel(stack);
        if (level == null || pos == null) return new ActionResult(ActionResult.Status.LINKED_NETWORK_NOT_FOUND, 0);

        // 弾薬数を更新
        ActionResult result = AE2LinkHelper.extractionAmmo(level, pos, stack, ammo, Integer.MAX_VALUE, Actionable.SIMULATE);
        // 弾薬箱の弾薬数を直接更新
        super.setAmmoCount(stack, result.count());
        // リンク状態を更新
        this.setLinked(stack, result.status() == ActionResult.Status.SUCCESS);

        return result;
    }

    @Override
    public void setAmmoCount(ItemStack ammoBox, int count) {
        //弾薬が減少している個数を計算
        int oldCount = this.getAmmoCount(ammoBox);
        int diff = oldCount - count;
        if (diff <= 0) return;

        // 弾薬箱が存在するレベルと座標を取得
        BlockPos pos = getPos(ammoBox);
        Level level = getLevel(ammoBox);
        if (level == null || pos == null) return;

        // 弾薬をAE2ネットワークから取り出す
        ItemStack ammo = AmmoItemBuilder.create().setId(getAmmoId(ammoBox)).setCount(1).build();
        AE2LinkHelper.extractionAmmo(level, pos, ammoBox, ammo, diff, Actionable.MODULATE);

        // エネルギーを消費
        extractAEPower(ammoBox, AppliedAmmoBoxConfig.AMMO_BOX_USE_POWER_PER_AMMO.get() * diff, Actionable.MODULATE);

        // 弾薬数を再取得して設定
        updateAmmoCount(ammoBox);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag advancedTooltips) {
        final CompoundTag tag = stack.getTag();
        double internalCurrentPower = 0;
        final double internalMaxPower = this.getAEMaxPower(stack);

        if (tag != null) {
            internalCurrentPower = tag.getDouble(CURRENT_POWER_NBT_KEY);
        }

        lines.add(Tooltips.energyStorageComponent(internalCurrentPower, internalMaxPower));

        if (isLinked(stack)) {
            lines.add(Tooltips.of(GuiText.Linked, Tooltips.GREEN));
        } else {
            lines.add(Tooltips.of(GuiText.Unlinked, Tooltips.RED));
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
    public boolean isBarVisible(ItemStack stack) {
        return true;
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
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new WirelessAmmoBoxCapabilityProvider(stack, this);
    }
}
