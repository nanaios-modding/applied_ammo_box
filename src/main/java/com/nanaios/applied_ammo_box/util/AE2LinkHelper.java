package com.nanaios.applied_ammo_box.util;

import appeng.api.config.Actionable;
import appeng.api.implementations.blockentities.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.StorageHelper;
import appeng.api.util.DimensionalBlockPos;
import appeng.blockentity.networking.WirelessAccessPointBlockEntity;
import appeng.me.helpers.BaseActionSource;
import appeng.me.helpers.ChannelPowerSrc;
import appeng.util.Platform;
import com.mojang.datafixers.util.Pair;
import com.nanaios.applied_ammo_box.AppliedAmmoBox;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
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

/// AE2のリンク関連のヘルパークラス
public class AE2LinkHelper {
    /// 座標データのNBTキー
    public static String TAG_ACCESS_POINT_POS = "accessPoint";
    /// リンク状態フラグのNBTキー
    public static String TAG_IS_LINKED = "isLinked";

    /// 弾薬の数をAE2ネットワークから取得し更新する \
    /// サーバーサイドでのみ動作
    /// @param pos 弾薬箱の座標
    /// @param ammoBox 弾薬箱のItemStack
    /// @param ammo 弾薬のItemStack
    /// @param count 更新する弾薬数の上限
    /// @param mode 抽出モード
    public static ActionResult.Wrapper extractionAmmo(GlobalPos pos, ItemStack ammoBox, ItemStack ammo, int count, Actionable mode) {
        // 座標を取得
        GlobalPos linkPos = AE2LinkHelper.getLinkedPosition(ammoBox);
        if (linkPos == null) return ActionResult.DEVICE_NOT_LINKED.set(0);

        // グリッドを取得
        IGrid grid = AE2LinkHelper.getGrid(linkPos);
        if (grid == null) return ActionResult.LINKED_NETWORK_NOT_FOUND.set(0);

        // 有効範囲内のアクセスポイントを取得
        IWirelessAccessPoint wap = AE2LinkHelper.getBestWap(grid, pos);
        if (wap == null) return ActionResult.LINKED_NETWORK_NOT_FOUND.set(0);

        // グリッドノードを取得
        IGridNode node = wap.getActionableNode();
        if (node == null) return ActionResult.LINKED_NETWORK_NOT_FOUND.set(0);

        // 弾薬のデータを生成
        IActionSource source = new BaseActionSource();
        AEKey key = AEItemKey.of(ammo);
        if (key == null) return ActionResult.LINKED_NETWORK_NOT_FOUND.set(0);

        // 弾薬の数を取得
        int ammoCount = (int) StorageHelper.poweredExtraction(new ChannelPowerSrc(node, grid.getEnergyService()), grid.getStorageService().getInventory(), key, count, source, mode);
        // 弾薬数を0以上に補正
        ammoCount = Math.max(0, ammoCount);

        return ActionResult.SUCCESS.set(ammoCount);
    }

    /// ItemStackからリンクされた座標を取得する
    /// @param item リンクされた座標を持つ可能性のあるItemStack
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
        if (linkedLevel == null) return null;

        // 座標からブロックエンティティを取得
        BlockEntity blockEntity = Platform.getTickingBlockEntity(linkedLevel, linkedPos.pos());
        if (!(blockEntity instanceof IWirelessAccessPoint accessPoint)) return null;

        return accessPoint.getGrid();
    }

    /// 有効範囲に指定座標が含まれるアクセスポイントの取得を試みる \
    /// サーバーサイドでのみ動作
    /// @param grid チェック対象のAE2グリッド
    /// @param pos チェック対象の座標
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static @Nullable IWirelessAccessPoint getBestWap(IGrid grid, GlobalPos pos) {
        IWirelessAccessPoint bestWap = null;
        double bestSqDistance = Double.MAX_VALUE;

        // 座標のレベルを取得
        ServerLevel level = ServerLifecycleHooks.getCurrentServer().getLevel(pos.dimension());
        if (level == null) return null;


        // 最も近いかつ有効なアクセスポイントを見つける
        for (WirelessAccessPointBlockEntity wap : grid.getMachines(WirelessAccessPointBlockEntity.class)) {
            double sqDistance = getWapSqDistance(wap,pos.pos(),level);
            if (sqDistance < bestSqDistance) {
                bestSqDistance = sqDistance;
                bestWap = wap;
            }
        }

        return bestWap;
    }

    /// アクセスポイントと指定された座標とで三平方を計算する \
    /// アクセスポイントがアクティブでない場合、またはLevelが異なる場合は無効な距離を返す \
    /// サーバーサイドでのみ動作
    /// @param wap 対象のアクセスポイント
    /// @param pos 距離を測定する座標
    /// @param level 座標が存在するレベル
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static double getWapSqDistance(WirelessAccessPointBlockEntity wap, BlockPos pos, ServerLevel level) {
        // アクセスポイントがアクティブでない場合は無効な距離を返す
        if(!wap.isActive()) return Double.MAX_VALUE;

        // アクセスポイントの座標とレベルを取得
        DimensionalBlockPos dc = wap.getLocation();
        // レベルが異なる場合は無効な距離を返す
        if(dc.getLevel() != level) return Double.MAX_VALUE;


        // アクセスポイントの範囲を取得
        double rangeLimit = wap.getRange();
        // 距離の二乗を計算し、三平方の定理で使用する
        rangeLimit *= rangeLimit;

        // 三平方の定理で距離の二乗を計算
        int offX = dc.getPos().getX() - pos.getX();
        int offY = dc.getPos().getY() - pos.getY();
        int offZ = dc.getPos().getZ() - pos.getZ();
        double r = offX * offX + offY * offY + offZ * offZ;

        // アクセスポイントの範囲内なら距離を返す
        if (r < rangeLimit) return r;

        // 範囲外なら無効な距離を返す
        return Double.MAX_VALUE;
    }

    /// ItemStackにリンク状態フラグを設定する \
    /// サーバーサイドでのみ動作
    /// @param stack リンク状態を設定するItemStack
    /// @param isLinked リンク状態フラグ
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static void setLinked(ItemStack stack, boolean isLinked) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(TAG_IS_LINKED, isLinked);
    }

    /// ItemStackのリンク状態を取得する
    /// @param stack リンク状態を確認するItemStack
    public static boolean isLinked(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.getBoolean(TAG_IS_LINKED);
    }

    public enum ActionResult {
        SUCCESS,
        DEVICE_NOT_LINKED,
        LINKED_NETWORK_NOT_FOUND;

        public Wrapper set(int count) {
            return new Wrapper(this, count);
        }

        public record Wrapper(ActionResult action, int count) { }
    }
}
