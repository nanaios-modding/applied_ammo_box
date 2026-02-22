package com.nanaios.applied_ammo_box.registries;

import com.nanaios.applied_ammo_box.AppliedAmmoBox;
import com.nanaios.applied_ammo_box.AppliedAmmoBoxLang;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;

public class AppliedAmmoBoxCreativeTabs {
    public static DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AppliedAmmoBox.MODID);

    static {
        TABS.register("applied_ammo_box_tab", () -> CreativeModeTab.builder()
                .title(AppliedAmmoBoxLang.CREATIVE_TAB_NAME.get())
                .icon(() -> new ItemStack(AppliedAmmoBoxItems.AMMO_BOX.get()))
                .displayItems((params, output) -> AppliedAmmoBoxItems.registerCreativeTab(output))
                .build()
        );
    }
}

