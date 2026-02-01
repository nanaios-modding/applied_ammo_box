package com.nanaios.applied_ammo_box.registries;

import appeng.api.config.Actionable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import com.nanaios.applied_ammo_box.AppliedAmmoBox;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class AppliedAmmoBoxCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AppliedAmmoBox.MODID);

    static {
        TABS.register("applied_ammo_box_tab",() -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + AppliedAmmoBox.MODID + ".creative_tab"))
                .icon(() -> new ItemStack(AppliedAmmoBoxItems.AMMO_BOX.get()))
                .displayItems((params, output) -> {
                    for(RegistryObject<Item> registry : AppliedAmmoBoxItems.ITEMS.getEntries()){
                        Item item = registry.get();
                        output.accept(item);

                        // 満充電のアイテムも追加する
                        if(item instanceof IAEItemPowerStorage powered) {
                            ItemStack poweredStack = new ItemStack(item,1);
                            powered.injectAEPower(poweredStack, powered.getAEMaxPower(poweredStack), Actionable.MODULATE);
                            output.accept(poweredStack);
                        }
                    }
                }).build()
        );
    }
}
