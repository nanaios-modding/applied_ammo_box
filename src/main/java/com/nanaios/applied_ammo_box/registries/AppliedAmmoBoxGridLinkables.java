package com.nanaios.applied_ammo_box.registries;

import appeng.api.features.GridLinkables;
import com.nanaios.applied_ammo_box.AppliedAmmoBox;
import com.nanaios.applied_ammo_box.util.LinkableHandler;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AppliedAmmoBoxGridLinkables {
	public static void register() {
		for (DeferredHolder<Item, ? extends Item> registry : AppliedAmmoBoxItems.WIRELESS_ITEMS.getEntries()) {
			AppliedAmmoBox.LOGGER.debug("Register {} with GridLinkables", registry.get());
			GridLinkables.register(registry.get(), new LinkableHandler());
		}
	}
}

