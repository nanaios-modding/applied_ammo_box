package com.nanaios.applied_ammo_box;

import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.items.tools.powered.powersink.PoweredItemCapabilities;
import com.mojang.logging.LogUtils;
import com.nanaios.applied_ammo_box.config.AppliedAmmoBoxConfig;
import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxCreativeTabs;
import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxDataComponents;
import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxGridLinkables;
import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxItems;
import com.tacz.guns.GunMod;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.slf4j.Logger;

@Mod(AppliedAmmoBox.MODID)
public class AppliedAmmoBox {

    public static final String MODID = "applied_ammo_box";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AppliedAmmoBox(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        //Register data components
        LOGGER.debug("Registering Applied Ammo Box data components...");
        AppliedAmmoBoxDataComponents.DATA_COMPONENTS.register(modEventBus);
        // Register items
        LOGGER.debug("Registering Applied Ammo Box items...");
        AppliedAmmoBoxItems.WIRELESS_ITEMS.register(modEventBus);
        AppliedAmmoBoxItems.FAKE_ITEMS.register(modEventBus);
        // Register creative tab
        LOGGER.debug("Registering Applied Ammo Box creative tab...");
        AppliedAmmoBoxCreativeTabs.TABS.register(modEventBus);

        // Register config
        LOGGER.debug("Registering Applied Ammo Box config...");
        modContainer.registerConfig(ModConfig.Type.COMMON, AppliedAmmoBoxConfig.SPEC);
    }

    @SubscribeEvent
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (object, context) -> new PoweredItemCapabilities(object, (IAEItemPowerStorage) AppliedAmmoBoxItems.AMMO_BOX.get()));
        event.registerItem(
                Capabilities.EnergyStorage.ITEM,
                (object, context) -> new PoweredItemCapabilities(object, (IAEItemPowerStorage) AppliedAmmoBoxItems.CREATIVE_AMMO_BOX.get()));
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Register linkables
        LOGGER.info("Registering Applied Ammo Box grid linkables...");
        event.enqueueWork(AppliedAmmoBoxGridLinkables::register);
    }

    @SubscribeEvent
    public void buildContents(BuildCreativeModeTabContentsEvent event) {
        ResourceLocation targetTabLocation = ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID, "other");
        if (!event.getTabKey().location().equals(targetTabLocation)) return;

        LOGGER.info("Registering Applied Ammo Box items to other creative tab...");
        AppliedAmmoBoxItems.registerCreativeTab(event);
    }
}
