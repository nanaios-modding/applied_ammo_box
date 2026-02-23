package com.nanaios.applied_ammo_box.registries;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import com.nanaios.applied_ammo_box.AppliedAmmoBox;
import com.nanaios.applied_ammo_box.item.CreativeWirelessAmmoBoxItem;
import com.nanaios.applied_ammo_box.item.TabIconItem;
import com.nanaios.applied_ammo_box.item.WirelessAmmoBoxItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AppliedAmmoBoxItems {
    public static final DeferredRegister<Item> WIRELESS_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AppliedAmmoBox.MODID);
    public static final DeferredRegister<Item> FAKE_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AppliedAmmoBox.MODID);

    /// タブのアイコンアイテム
    public static final RegistryObject<Item> ICON = FAKE_ITEMS.register("tab_icon", TabIconItem::new);

    /// アイテムの登録
    static {
        WIRELESS_ITEMS.register("ammo_box", WirelessAmmoBoxItem::new);
        WIRELESS_ITEMS.register("creative_ammo_box", CreativeWirelessAmmoBoxItem::new);
    }

    /// クリエイティブタブにアイテムを登録する
    ///
    /// @param output アイテムを登録するためのoutput
    public static void registerCreativeTab(CreativeModeTab.Output output) {
        for (RegistryObject<Item> registry : WIRELESS_ITEMS.getEntries()) {
            Item item = registry.get();
            output.accept(item);

            // 満充電のアイテムも追加する
            if (item instanceof IAEItemPowerStorage powered) {
                ItemStack poweredStack = new ItemStack(item, 1);
                if (powered.getPowerFlow(poweredStack) == AccessRestriction.NO_ACCESS) continue;
                powered.injectAEPower(poweredStack, powered.getAEMaxPower(poweredStack), Actionable.MODULATE);
                output.accept(poweredStack);
            }
        }
    }
}
