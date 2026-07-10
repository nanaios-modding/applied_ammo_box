package com.nanaios.applied_ammo_box.registries;

import com.mojang.serialization.Codec;
import com.nanaios.applied_ammo_box.AppliedAmmoBox;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AppliedAmmoBoxDataComponents {

    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, AppliedAmmoBox.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> AMMO_BOX_LEVEL = DATA_COMPONENTS.registerComponentType(
            "ammo_box_level",
            builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> AMMO_BOX_BLOCK_POS = DATA_COMPONENTS.registerComponentType(
            "ammo_box_block_pos",
            builder -> builder
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> AMMO_BOX_LINKED = DATA_COMPONENTS.registerComponentType(
            "ammo_box_linked",
            builder -> builder
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
    );
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> AMMO_BOX_REFRESH = DATA_COMPONENTS.registerComponentType(
            "ammo_box_refresh",
            builder -> builder
                    .persistent(Codec.LONG)
                    .networkSynchronized(ByteBufCodecs.VAR_LONG)
    );

}
