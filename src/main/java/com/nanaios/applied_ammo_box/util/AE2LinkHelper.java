package com.nanaios.applied_ammo_box.util;

import appeng.api.implementations.blockentities.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.util.Platform;
import com.mojang.datafixers.util.Pair;
import com.nanaios.applied_ammo_box.AppliedAmmoBox;
import net.minecraft.Util;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

public class AE2LinkHelper {
    /// ItemStackのNBTタグで使用する座標データのキー
    public static String TAG_ACCESS_POINT_POS = "accessPoint";

    /// ItemStackからリンクされた座標を取得する \
    /// サーバーサイドでのみ動作
    /// @param item リンクされた座標を持つ可能性のあるItemStack
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static @Nullable GlobalPos getLinkedPosition(ItemStack item) {
        CompoundTag tag = item.getTag();
        if (tag != null && tag.contains(TAG_ACCESS_POINT_POS, Tag.TAG_COMPOUND)) {
            return GlobalPos.CODEC.decode(NbtOps.INSTANCE, tag.get(TAG_ACCESS_POINT_POS))
                    .resultOrPartial(Util.prefix("Linked position", AppliedAmmoBox.LOGGER::error))
                    .map(Pair::getFirst)
                    .orElse(null);
        } else {
            return null;
        }
    }

    /// 座標からAE2のグリッドを取得する \
    /// サーバーサイドでのみ動作
    /// @param linkedPos AE2のアクセスポイントの座標
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static @Nullable IGrid getGrid(GlobalPos linkedPos) {
        // リンクされた座標のレベルを取得
        ServerLevel linkedLevel = ServerLifecycleHooks.getCurrentServer().getLevel(linkedPos.dimension());
        if (linkedLevel == null) {
            return null;
        }

        // 座標からブロックエンティティを取得
        BlockEntity blockEntity = Platform.getTickingBlockEntity(linkedLevel, linkedPos.pos());
        if (!(blockEntity instanceof IWirelessAccessPoint accessPoint)) {
            return null;
        }

        return accessPoint.getGrid();
    }
}
