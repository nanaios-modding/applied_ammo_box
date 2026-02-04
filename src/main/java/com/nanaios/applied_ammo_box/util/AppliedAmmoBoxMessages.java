package com.nanaios.applied_ammo_box.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum AppliedAmmoBoxMessages {
    CLEAR_AMMO_DATA("cleared_ammo_data");

    public final String path;
    AppliedAmmoBoxMessages(String path) {
        this.path = path;
    }

    public MutableComponent get() {
        return Component.translatable("chat.applied_ammo_box." + path);
    }
}
