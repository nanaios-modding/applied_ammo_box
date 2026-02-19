package com.nanaios.applied_ammo_box.registries;

import com.nanaios.applied_ammo_box.AppliedAmmoBox;
import com.nanaios.applied_ammo_box.AppliedAmmoBoxLang;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegisterEvent;

import static com.nanaios.applied_ammo_box.AppliedAmmoBox.LOGGER;

public class AppliedAmmoBoxCreativeTabs {

    public static void register(RegisterEvent.RegisterHelper<CreativeModeTab> helper) {
            LOGGER.info("Registering Applied Ammo Box creative tabs...");
            helper.register(
                    ResourceLocation.fromNamespaceAndPath(AppliedAmmoBox.MODID,"applied_ammo_box_tab"),
                    CreativeModeTab.builder()
                            .title(AppliedAmmoBoxLang.CREATIVE_TAB_NAME.get())
                            .icon(() -> new ItemStack(AppliedAmmoBoxItems.AMMO_BOX.get()))
                            .displayItems((params, output) -> {})
                            .build()
            );
    }
}

