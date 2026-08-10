package com.nanaios.applied_ammo_box.item;

import appeng.api.config.Actionable;
import appeng.core.localization.GuiText;
import appeng.core.localization.PlayerMessages;
import appeng.core.localization.Tooltips;
import com.nanaios.applied_ammo_box.AppliedAmmoBoxLang;
import com.nanaios.applied_ammo_box.config.AppliedAmmoBoxConfig;
import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxDataComponents;
import com.nanaios.applied_ammo_box.util.AE2LinkHelper;
import com.nanaios.applied_ammo_box.util.AE2LinkHelper.ActionResult;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.item.AmmoBoxItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WirelessAmmoBoxItem extends AmmoBoxItem implements IDefaultAEItemPowerStorage, ITimeStamp, ILinkableItem {

	protected static final int DEFAULT_GREEN = Mth.hsvToRgb(1 / 3.0F, 1.0F, 1.0F);

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
		//Get the item stack
		ItemStack stack = player.getItemInHand(hand);
		//Reset ammo data if the player is crouching
		if (player.isCrouching()) {
			if (!level.isClientSide) {
				clearAmmoData(stack);
				player.displayClientMessage(AppliedAmmoBoxLang.CLEAR_AMMO_MESSAGE.get(), true);
			}
			return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
		}

		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
		super.inventoryTick(stack, level, entity, slotId, isSelected);

		if (this.getAECurrentPower(stack) <= 0) {
			super.setAmmoCount(stack, 0);
			setAmmoId(stack, DefaultAssets.EMPTY_AMMO_ID);
			return;
		}

		//Return if not on the server
		if (level.isClientSide()) return;

		//Return if not in a player inventory
		if (!(entity instanceof Player player)) return;

		//Get the ammo id of the gun the player is holding
		ItemStack iGunStack = player.getItemInHand(InteractionHand.MAIN_HAND);
		boolean isUpdate = updateAmmoId(stack, iGunStack);

		//Only update once a second
		if (isWantUpdate(stack) || isUpdate) {
			//Get ammo box coordinates
			setPos(stack, player.blockPosition());
			setLevel(stack, level);

			//Update time stamp
			setTimeStamp(stack, System.currentTimeMillis());

			//Update ammo count
			ActionResult result = updateAmmoCount(stack);

			switch (result.status()) {
				case DEVICE_NOT_LINKED -> player.displayClientMessage(PlayerMessages.DeviceNotLinked.text(), true);
				case LINKED_NETWORK_NOT_FOUND ->
						player.displayClientMessage(PlayerMessages.LinkedNetworkNotFound.text(), true);
			}
		}
	}

	/// Checks and updates the current ammo ID, returns true if update
	public boolean updateAmmoId(ItemStack ammoBox, ItemStack gunStack) {
		// Return false if the player isn't holding a gun
		if (!(gunStack.getItem() instanceof IGun gun)) return false;

		// Get the ammo ID
		ResourceLocation ammoId = TimelessAPI.getCommonGunIndex(gun.getGunId(gunStack))
				.map(commonGunIndex -> commonGunIndex.getGunData().getAmmoId())
				.orElse(DefaultAssets.EMPTY_AMMO_ID);

		// Cancel if the ID is the same
		if (ammoId.equals(getAmmoId(ammoBox))) return false;

		// Update the ID
		setAmmoId(ammoBox, ammoId);
		return true;
	}

	/// Checks if it's been at least a second since the last update
	public boolean isWantUpdate(ItemStack stack) {
		return (System.currentTimeMillis() - getTimeStamp(stack)) > 1000;
	}

	/// Set the current coordinates
	public void setPos(ItemStack stack, BlockPos pos) {
		stack.set(AppliedAmmoBoxDataComponents.AMMO_BOX_BLOCK_POS, pos.asLong());
	}

	/// Set the ammo box's level
	public void setLevel(ItemStack stack, Level level) {
		stack.set(AppliedAmmoBoxDataComponents.AMMO_BOX_LEVEL, level.dimension().location().toString());
	}

	/// Get how many bullets of a given ID are in the network
	public ActionResult updateAmmoCount(ItemStack stack) {
		// Get the ammo info
		ItemStack ammo = AmmoItemBuilder.create().setId(getAmmoId(stack)).setCount(1).build();

		// Makes sure the box is connected to a network
		BlockPos pos = getPos(stack);
		Level level = getLevel(stack);
		if (level == null || pos == null) return new ActionResult(ActionResult.Status.LINKED_NETWORK_NOT_FOUND, 0);

		// Update the ammo count
		ActionResult result = AE2LinkHelper.extractionAmmo(level, pos, stack, ammo, Integer.MAX_VALUE, Actionable.SIMULATE);
		// Update the ammo count in the box itself
		super.setAmmoCount(stack, result.count());
		// Update the connection status
		stack.set(AppliedAmmoBoxDataComponents.AMMO_BOX_LINKED, result.status() == ActionResult.Status.SUCCESS);

		return result;
	}

	/// Get the current coordinates
	@Nullable
	public BlockPos getPos(ItemStack stack) {
		return BlockPos.of(stack.getOrDefault(AppliedAmmoBoxDataComponents.AMMO_BOX_BLOCK_POS, 0L));
	}

	/// Get the current level
	@Nullable
	public Level getLevel(ItemStack stack) {
		return ServerLifecycleHooks.getCurrentServer().getLevel(
				ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(stack.getOrDefault(AppliedAmmoBoxDataComponents.AMMO_BOX_LEVEL, "minecraft:overworld")))
		);
	}

	public void clearAmmoData(ItemStack stack) {
		stack.remove(DataComponents.CUSTOM_DATA);
	}

	@Override
	public void setAmmoCount(ItemStack ammoBox, int count) {
		//Calculate how many bullets to subtract
		int oldCount = this.getAmmoCount(ammoBox);
		int diff = oldCount - count;
		if (diff <= 0) return;

		// Makes sure the box is connected to a network
		BlockPos pos = getPos(ammoBox);
		Level level = getLevel(ammoBox);
		if (level == null || pos == null) return;

		// Remove ammo from the connected network
		ItemStack ammo = AmmoItemBuilder.create().setId(getAmmoId(ammoBox)).setCount(1).build();
		AE2LinkHelper.extractionAmmo(level, pos, ammoBox, ammo, diff, Actionable.MODULATE);

		// Consume stored power
		extractAEPower(ammoBox, AppliedAmmoBoxConfig.AMMO_BOX_PER_ROUND_POWER_USAGE.get() * diff, Actionable.MODULATE);

		// Get and sets the ammo count in the box
		updateAmmoCount(ammoBox);
	}

	@Override
	public boolean isAmmoBoxOfGun(ItemStack gun, ItemStack ammoBox) {
		if (getAmmoCount(ammoBox) <= 0) return false;
		return super.isAmmoBoxOfGun(gun, ammoBox);
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
	public @NotNull Component getName(ItemStack stack) {
		return AppliedAmmoBoxLang.WIRELESS_AMMO_BOX_NAME.get();
	}

	@Override
	public int getBarColor(ItemStack stack) {
		// This is the standard green color of full durability bars
		return DEFAULT_GREEN;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
		tooltipComponents.add(Tooltips.energyStorageComponent(getAECurrentPower(stack), getAEMaxPower(stack)));

		if (AE2LinkHelper.getLinkedPosition(stack) != null) {
			tooltipComponents.add(Tooltips.of(GuiText.Linked, Tooltips.GREEN));
		} else {
			tooltipComponents.add(Tooltips.of(GuiText.Unlinked, Tooltips.RED));
		}
	}

	@Override
	public boolean shouldCauseReequipAnimation(@NotNull ItemStack oldStack, @NotNull ItemStack newStack, boolean slotChanged) {
		return slotChanged || !ItemStack.isSameItem(oldStack, newStack);
	}
}
