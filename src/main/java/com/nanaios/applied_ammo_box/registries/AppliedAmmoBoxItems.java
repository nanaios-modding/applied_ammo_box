package com.nanaios.applied_ammo_box.registries;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import com.nanaios.applied_ammo_box.AppliedAmmoBox;
import com.nanaios.applied_ammo_box.item.CreativeWirelessAmmoBoxItem;
import com.nanaios.applied_ammo_box.item.TabIconItem;
import com.nanaios.applied_ammo_box.item.WirelessAmmoBoxItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AppliedAmmoBoxItems {

	public static final DeferredRegister.Items WIRELESS_ITEMS = DeferredRegister.createItems(AppliedAmmoBox.MODID);
	public static final DeferredRegister.Items FAKE_ITEMS = DeferredRegister.createItems(AppliedAmmoBox.MODID);

	/// Tab icon item
	public static final DeferredItem<TabIconItem> ICON = FAKE_ITEMS.register("tab_icon", TabIconItem::new);

	/// Register Items
	public static final DeferredItem<Item> AMMO_BOX = WIRELESS_ITEMS.register("ammo_box", WirelessAmmoBoxItem::new);
	public static final DeferredItem<Item> CREATIVE_AMMO_BOX = WIRELESS_ITEMS.register("creative_ammo_box", CreativeWirelessAmmoBoxItem::new);

	/// Add items to a creative tab
	public static void registerCreativeTab(CreativeModeTab.Output output) {
		for (DeferredHolder<Item, ? extends Item> registry : WIRELESS_ITEMS.getEntries()) {
			Item item = registry.get();
			output.accept(item);


			//Add charged items to tab
			if (item instanceof IAEItemPowerStorage powered) {
				ItemStack poweredStack = new ItemStack(item, 1);
				if (powered.getPowerFlow(poweredStack) == AccessRestriction.NO_ACCESS) continue;
				powered.injectAEPower(poweredStack, powered.getAEMaxPower(poweredStack), Actionable.MODULATE);
				output.accept(poweredStack);
			}
		}
	}
}
