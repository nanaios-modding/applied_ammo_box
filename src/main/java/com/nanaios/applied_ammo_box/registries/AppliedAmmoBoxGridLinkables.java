package com.nanaios.applied_ammo_box.registries;

import appeng.api.features.GridLinkables;
import com.nanaios.applied_ammo_box.util.LinkableHandler;

public class AppliedAmmoBoxGridLinkables {
    public static void register() {

        GridLinkables.register(AppliedAmmoBoxItems.AMMO_BOX.get(), new LinkableHandler());
        GridLinkables.register(AppliedAmmoBoxItems.CREATIVE_AMMO_BOX.get(), new LinkableHandler());
    }
}
