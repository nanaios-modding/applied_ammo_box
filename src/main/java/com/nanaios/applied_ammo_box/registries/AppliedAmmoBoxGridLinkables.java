package com.nanaios.applied_ammo_box.registries;

import appeng.api.features.GridLinkables;
import com.nanaios.applied_ammo_box.util.LinkableHandler;
import net.minecraft.world.item.Item;

public class AppliedAmmoBoxGridLinkables {
    public static void register() {
        Item item = AppliedAmmoBoxItems.AMMO_BOX.get();
        GridLinkables.register(item,new LinkableHandler());
    }
}
