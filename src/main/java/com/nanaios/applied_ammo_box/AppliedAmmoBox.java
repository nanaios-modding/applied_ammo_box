package com.nanaios.applied_ammo_box;

import com.nanaios.applied_ammo_box.config.AppliedAmmoBoxConfig;
import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxCreativeTabs;
import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxGridLinkables;
import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxItems;
import com.tacz.guns.GunMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AppliedAmmoBox.MODID)
@Mod.EventBusSubscriber(modid = AppliedAmmoBox.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AppliedAmmoBox {
    public static final String MODID = "applied_ammo_box";

    public static final Logger LOGGER = LogManager.getLogger();

    public AppliedAmmoBox(FMLJavaModLoadingContext context) {
        // コンフィグの登録
        LOGGER.info("Registering Applied Ammo Box config...");
        context.registerConfig(ModConfig.Type.COMMON,AppliedAmmoBoxConfig.init());

        // アイテムの登録
        LOGGER.info("Registering Applied Ammo Box items...");
        IEventBus modEventBus = context.getModEventBus();
        AppliedAmmoBoxItems.WIRELESS_ITEMS.register(modEventBus);
        AppliedAmmoBoxItems.FAKE_ITEMS.register(modEventBus);

        // クリエイティブタブの登録
        LOGGER.info("Registering Applied Ammo Box creative tabs...");
        AppliedAmmoBoxCreativeTabs.TABS.register(modEventBus);
    }

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        // リンク可能なアイテムの登録
        LOGGER.info("Registering Applied Ammo Box grid linkables...");
        event.enqueueWork(AppliedAmmoBoxGridLinkables::register);
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        ResourceLocation targetTabLocation = ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID,"other");
        if(!event.getTabKey().location().equals(targetTabLocation)) return;

        LOGGER.info("Registering Applied Ammo Box items to other creative tab...");
        AppliedAmmoBoxItems.registerCreativeTab(event);
    }
}
