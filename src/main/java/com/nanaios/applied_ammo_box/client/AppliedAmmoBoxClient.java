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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

@Mod(value = AppliedAmmoBox.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = AppliedAmmoBox.MODID, value = Dist.CLIENT)
public class AppliedAmmoBoxClient {
	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		for (DeferredHolder<Item, ? extends Item> item : AppliedAmmoBoxItems.WIRELESS_ITEMS.getEntries()) {
			ItemProperties.register(
					item.get(),
					ResourceLocation.fromNamespaceAndPath(AppliedAmmoBox.MODID, "linked"),
					AppliedAmmoBoxClient::hasLight
			);
		}
	}

	public static float hasLight(ItemStack stack, ClientLevel level, LivingEntity entity, int seed) {
		if (stack.getItem() instanceof WirelessAmmoBoxItem wirelessItem
				&& wirelessItem.isLinked(stack)
				&& wirelessItem.getAECurrentPower(stack) > 0
		) {
			return 1.0f;
		}
		return 0.0f;
	}

}
