package com.nanaios.applied_ammo_box.registries;

import com.nanaios.applied_ammo_box.AppliedAmmoBox;
import com.nanaios.applied_ammo_box.AppliedAmmoBoxLang;
import com.nanaios.applied_ammo_box.config.AppliedAmmoBoxConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.RegisterEvent;

public class AppliedAmmoBoxCreativeTabs {

    public static void register(RegisterEvent.RegisterHelper<CreativeModeTab> helper) {

        // クリエイティブタブの生成が有効でない場合は、登録をスキップ
        if(!AppliedAmmoBoxConfig.GENERATE_CREATIVE_TAB.get()) return;

        helper.register(
                ResourceLocation.fromNamespaceAndPath(AppliedAmmoBox.MODID,"applied_ammo_box_tab"),
                CreativeModeTab.builder()
                        .title(AppliedAmmoBoxLang.CREATIVE_TAB_NAME.get())
                        .icon(() -> new ItemStack(AppliedAmmoBoxItems.AMMO_BOX.get()))
                        .displayItems((params, output) -> AppliedAmmoBoxItems.registerCreativeTab(output))
                        .build()
        );
    }
}
