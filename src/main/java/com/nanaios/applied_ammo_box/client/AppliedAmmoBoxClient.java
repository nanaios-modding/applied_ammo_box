package com.nanaios.applied_ammo_box.client;

import com.nanaios.applied_ammo_box.AppliedAmmoBox;
import com.nanaios.applied_ammo_box.item.WirelessAmmoBoxItem;
import com.nanaios.applied_ammo_box.registries.AppliedAmmoBoxItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = AppliedAmmoBox.MODID)
public class AppliedAmmoBoxClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        ItemProperties.register(
                AppliedAmmoBoxItems.AMMO_BOX.get(),
                ResourceLocation.fromNamespaceAndPath(AppliedAmmoBox.MODID, "linked"),
                (stack, level, entity, seed) -> {
                    if (stack.getItem() instanceof WirelessAmmoBoxItem item) {
                        if (item.isLinked(stack) && item.getAECurrentPower(stack) > 0) {
                            return 1.0f;
                        } else {
                            return 0.0f;
                        }
                    }
                    return 1.0f;
                });

    }
}
