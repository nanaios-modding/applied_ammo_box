package com.nanaios.applied_ammo_box.item;

import appeng.api.features.IGridLinkableHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface ILinkableItem {
    /// リンク状態フラグのNBTキー
    String TAG_IS_LINKED = "isLinked";

    /// リンクハンドラーを取得する
    IGridLinkableHandler getLinkableHandler();

    /// ItemStackにリンク状態フラグを設定する
    /// @param stack リンク状態を設定するItemStack
    /// @param isLinked リンク状態フラグ
    default void setLinked(ItemStack stack, boolean isLinked) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(TAG_IS_LINKED, isLinked);
    }

    /// ItemStackのリンク状態を取得する
    /// @param stack リンク状態を確認するItemStack
    default boolean isLinked(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_IS_LINKED);
    }
}
