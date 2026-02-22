package com.nanaios.applied_ammo_box.registries;

import appeng.api.features.GridLinkables;
import com.nanaios.applied_ammo_box.util.LinkableHandler;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class AppliedAmmoBoxGridLinkables {
    public static void register() {
        for (RegistryObject<Item> registry : AppliedAmmoBoxItems.ITEMS.getEntries()) {
            GridLinkables.register(registry.get(), new LinkableHandler());
            GridLinkables.register(registry.get(), new LinkableHandler());
        }
    }
}
