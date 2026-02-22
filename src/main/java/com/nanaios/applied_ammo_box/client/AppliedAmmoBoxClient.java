package com.nanaios.applied_ammo_box.client;

import com.nanaios.applied_ammo_box.AppliedAmmoBox;
import com.nanaios.applied_ammo_box.item.WirelessAmmoBoxItem;
import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxItems;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = AppliedAmmoBox.MODID)
public class AppliedAmmoBoxClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        for(RegistryObject<Item> registry : AppliedAmmoBoxItems.WIRELESS_ITEMS.getEntries()) {
            ItemProperties.register(
                    registry.get(),
                    ResourceLocation.fromNamespaceAndPath(AppliedAmmoBox.MODID, "linked"),
                    AppliedAmmoBoxClient::isLighting
            );
        }
    }

    public static float isLighting(ItemStack stack, ClientLevel level, LivingEntity entity,int seed) {
        if (stack.getItem() instanceof WirelessAmmoBoxItem wirelessItem) {
            if (wirelessItem.isLinked(stack) && wirelessItem.getAECurrentPower(stack) > 0) {
                return 1.0f;
            } else {
                return 0.0f;
            }
        }
        return 1.0f;
    }
}
