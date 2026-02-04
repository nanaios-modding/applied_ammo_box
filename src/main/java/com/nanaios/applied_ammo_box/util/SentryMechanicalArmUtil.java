package com.nanaios.applied_ammo_box.util;

import com.nanaios.applied_ammo_box.item.WirelessAmmoBoxItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class SentryMechanicalArmUtil {

    /// SentryArmBlockEntityに設置されているワイヤレス弾薬箱の情報を更新する
    ///
    /// @param level レベル
    /// @param attachedAmmoBoxes 設置されている弾薬箱のリスト
    public static void updateAttachedAmmoBoxes(@NotNull Level level, NonNullList<ItemStack> attachedAmmoBoxes) {
        // サーバーサイドでのみ処理を行う
        if(level.isClientSide) return;

        for(ItemStack itemStack : attachedAmmoBoxes) {
            if(itemStack.isEmpty()) continue;
            if(!(itemStack.getItem() instanceof WirelessAmmoBoxItem wirelessAmmoBoxItem)) continue;
            if(!wirelessAmmoBoxItem.isWantUpdate(itemStack)) continue;

            // 設置されたタイムスタンプを更新する
            wirelessAmmoBoxItem.setTimeStamp(itemStack, System.currentTimeMillis());

            wirelessAmmoBoxItem.updateAmmoCount(itemStack);
        }
    }
}
