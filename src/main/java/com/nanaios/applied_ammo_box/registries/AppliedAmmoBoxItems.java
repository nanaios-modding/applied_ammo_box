package com.nanaios.applied_ammo_box.registries;

import appeng.api.config.Actionable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import com.nanaios.applied_ammo_box.AppliedAmmoBox;
import com.nanaios.applied_ammo_box.item.CreativeWirelessAmmoBoxItem;
import com.nanaios.applied_ammo_box.item.WirelessAmmoBoxItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AppliedAmmoBoxItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AppliedAmmoBox.MODID);

    public static RegistryObject<Item> AMMO_BOX = ITEMS.register("ammo_box", WirelessAmmoBoxItem::new);
    public static RegistryObject<CreativeWirelessAmmoBoxItem> CREATIVE_AMMO_BOX = ITEMS.register("creative_ammo_box", CreativeWirelessAmmoBoxItem::new);

    public static void registerCreativeTab(CreativeModeTab.Output output) {
        for (RegistryObject<Item> registry : ITEMS.getEntries()) {
            Item item = registry.get();
            output.accept(item);

            // 満充電のアイテムも追加する
            if (item instanceof IAEItemPowerStorage powered) {
                ItemStack poweredStack = new ItemStack(item, 1);
                powered.injectAEPower(poweredStack, powered.getAEMaxPower(poweredStack), Actionable.MODULATE);
                output.accept(poweredStack);
            }
        }
    }
}
