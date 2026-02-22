package com.nanaios.applied_ammo_box;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum AppliedAmmoBoxLang {
    CREATIVE_TAB_NAME("itemGroup.applied_ammo_box.creative_tab"),
    CLEAR_AMMO_MESSAGE("chat.applied_ammo_box.cleared_ammo_data"),
    WIRELESS_AMMO_BOX_NAME("item.applied_ammo_box.ammo_box");


    final String key;

    AppliedAmmoBoxLang(String key) {
        this.key = key;
    }

    public MutableComponent get(Object... args) {
        return Component.translatable(key, args);
    }
}
