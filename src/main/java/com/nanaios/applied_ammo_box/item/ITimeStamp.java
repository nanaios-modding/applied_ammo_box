package com.nanaios.applied_ammo_box.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/// タイムスタンプを管理するためのインターフェース
public interface ITimeStamp {
    /// タイムスタンプのNBTキー
    String TAG_TIME_STAMP = "lastCheckedTimeStamp";

    /// タイムスタンプを取得する
    /// @param stack 対象のItemStack
    default long getTimeStamp(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        return tag.getLong(TAG_TIME_STAMP);
    }

    /// タイムスタンプを設定する
    /// @param stack 対象のItemStack
    /// @param value 設定するタイムスタンプの値
    default void setTimeStamp(ItemStack stack, long value) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putLong(TAG_TIME_STAMP,value);
    }
}
