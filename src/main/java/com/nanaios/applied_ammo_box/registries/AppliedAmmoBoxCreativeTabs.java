package com.nanaios.applied_ammo_box.registries;

import com.nanaios.applied_ammo_box.AppliedAmmoBox;
import com.nanaios.applied_ammo_box.AppliedAmmoBoxLang;
import com.nanaios.applied_ammo_box.item.CreativeWirelessAmmoBoxItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;

public class AppliedAmmoBoxCreativeTabs {
    public static DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AppliedAmmoBox.MODID);

    static {
        TABS.register("applied_ammo_box_tab", () -> CreativeModeTab.builder()
                .title(AppliedAmmoBoxLang.CREATIVE_TAB_NAME.get())
                .icon(() -> {
                    CreativeWirelessAmmoBoxItem item = AppliedAmmoBoxItems.CREATIVE_AMMO_BOX.get();
                    ItemStack icon = new ItemStack(item);
                    item.setLinked(icon, true);
                    CompoundTag tag = icon.getTag();
                    if(tag != null) {
                        tag.putBoolean(CreativeWirelessAmmoBoxItem.NBT_FOILED, false);
                    }
                    return icon;

                })
                .displayItems((params, output) -> AppliedAmmoBoxItems.registerCreativeTab(output))
                .build()
        );
    }
}

