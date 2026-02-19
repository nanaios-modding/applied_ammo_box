package com.nanaios.applied_ammo_box;

import com.nanaios.applied_ammo_box.config.AppliedAmmoBoxConfig;
import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxCreativeTabs;
import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxGridLinkables;
import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxItems;
import com.tacz.guns.GunMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AppliedAmmoBox.MODID)
public class AppliedAmmoBox {
    public static final String MODID = "applied_ammo_box";

    public static final Logger LOGGER = LogManager.getLogger();

    public AppliedAmmoBox(FMLJavaModLoadingContext context) {
        // コンフィグの登録
        context.registerConfig(ModConfig.Type.COMMON,AppliedAmmoBoxConfig.init());

        // アイテムやタブの登録
        IEventBus modEventBus = context.getModEventBus();
        AppliedAmmoBoxItems.ITEMS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // リンク可能なアイテムの登録
        event.enqueueWork(AppliedAmmoBoxGridLinkables::register);
    }

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {
        // クリエイティブタブのレジストリであることを確認
        if (!event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) return;

        // クリエイティブタブの登録
        event.register(Registries.CREATIVE_MODE_TAB, AppliedAmmoBoxCreativeTabs::register);
    }

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        ResourceLocation targetTabLocation = ResourceLocation.fromNamespaceAndPath(GunMod.MOD_ID,"other");
        if(!event.getTabKey().location().equals(targetTabLocation)) return;

        AppliedAmmoBoxItems.registerCreativeTab(event);
    }
}
