package com.nanaios.applied_ammo_box.util;

import appeng.api.config.Actionable;
import appeng.api.ids.AEComponents;
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
import appeng.util.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

/// AE2 link-related helper class
public class AE2LinkHelper {

	/**
	 * Get ammo from the linked network
	 *
	 * @param pos   ammo box coordinates
	 * @param count max number of rounds to replenish
	 * @param mode  extraction mode
	 */
	public static ActionResult extractionAmmo(Level level, BlockPos pos, ItemStack ammoBox, ItemStack ammo, int count, Actionable mode) {
		//Get coordinates
		GlobalPos linkPos = AE2LinkHelper.getLinkedPosition(ammoBox);
		if (linkPos == null) return new ActionResult(ActionResult.Status.DEVICE_NOT_LINKED, 0);

		//Get grid
		IGrid grid = AE2LinkHelper.getGrid(linkPos);
		if (grid == null) return new ActionResult(ActionResult.Status.LINKED_NETWORK_NOT_FOUND, 0);

		//Find access points within range
		IWirelessAccessPoint wap = AE2LinkHelper.getBestWap(grid, level, pos);
		if (wap == null) return new ActionResult(ActionResult.Status.LINKED_NETWORK_NOT_FOUND, 0);

		//Get grid node
		IGridNode node = wap.getActionableNode();
		if (node == null) return new ActionResult(ActionResult.Status.LINKED_NETWORK_NOT_FOUND, 0);

		//Get ammo data
		IActionSource source = new BaseActionSource();
		AEKey key = AEItemKey.of(ammo);
		if (key == null) return new ActionResult(ActionResult.Status.SUCCESS, 0);

		//Get ammo amount
		int ammoCount = (int) StorageHelper.poweredExtraction(grid.getEnergyService(), grid.getStorageService().getInventory(), key, count, source, mode);
		//Make sure ammo amount is not negative
		ammoCount = Math.max(0, ammoCount);

		return new ActionResult(ActionResult.Status.SUCCESS, ammoCount);
	}

	/**
	 * Get linked coordinates from an item stack
	 *
	 * @param item item stack that might have linked coordinates
	 */
	public static @Nullable GlobalPos getLinkedPosition(ItemStack item) {
		return item.get(AEComponents.WIRELESS_LINK_TARGET);
	}

	/**
	 * Get AE2 grid from coordinates
	 *
	 * @param linkedPos access point coordinates
	 */
	public static @Nullable IGrid getGrid(GlobalPos linkedPos) {
		// Get coordinates' level
		ServerLevel linkedLevel = ServerLifecycleHooks.getCurrentServer().getLevel(linkedPos.dimension());
		if (linkedLevel == null) return null;

		// Get a block entity from the coordinates
		BlockEntity blockEntity = Platform.getTickingBlockEntity(linkedLevel, linkedPos.pos());
		if (!(blockEntity instanceof IWirelessAccessPoint accessPoint)) return null;

		return accessPoint.getGrid();
	}

	/**
	 * Try to find access points that have coverage over the given coordinates
	 *
	 * @param grid AE2 grid to be checked
	 * @param pos  Coordinates to be checked
	 */
	public static @Nullable IWirelessAccessPoint getBestWap(IGrid grid, Level level, BlockPos pos) {
		IWirelessAccessPoint bestWap = null;
		double bestSqDistance = Double.MAX_VALUE;

		// Find the nearest valid access point
		for (WirelessAccessPointBlockEntity wap : grid.getMachines(WirelessAccessPointBlockEntity.class)) {
			double sqDistance = getWapSqDistance(wap, pos, level);
			if (sqDistance < bestSqDistance) {
				bestSqDistance = sqDistance;
				bestWap = wap;
			}
		}

		return bestWap;
	}

	/**
	 * Calculate the distance between the access point and given coordinates \
	 * Returns an invalid distance if the access point is not active or in a different level
	 *
	 * @param wap   target access point
	 * @param level level where the coordinates are located
	 */
	public static double getWapSqDistance(WirelessAccessPointBlockEntity wap, BlockPos pos, Level level) {
		// Check if the access point is active or not
		if (!wap.isActive()) return Double.MAX_VALUE;

		// Get the access point's level
		DimensionalBlockPos dc = wap.getLocation();
		// Check if the access point is in the same level or not
		if (dc.getLevel() != level) return Double.MAX_VALUE;


		// Get the access point's range
		double rangeLimit = wap.getRange();
		rangeLimit *= rangeLimit;
		int offX = dc.getPos().getX() - pos.getX();
		int offY = dc.getPos().getY() - pos.getY();
		int offZ = dc.getPos().getZ() - pos.getZ();
		double r = offX * offX + offY * offY + offZ * offZ;

		// Check if the access point is within range
		if (r < rangeLimit) return r;

		// Return an invalid instance otherwise
		return Double.MAX_VALUE;
	}

	public record ActionResult(Status status, int count) {
		public enum Status {
			SUCCESS,
			DEVICE_NOT_LINKED,
			LINKED_NETWORK_NOT_FOUND
		}
	}
}