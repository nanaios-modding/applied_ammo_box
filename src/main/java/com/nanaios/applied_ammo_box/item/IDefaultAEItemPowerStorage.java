package com.nanaios.applied_ammo_box.item;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.ids.AEComponents;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.core.AEConfig;
import net.minecraft.world.item.ItemStack;

public interface IDefaultAEItemPowerStorage extends IAEItemPowerStorage {
    double MIN_POWER = 0.0001;

    @Override
    default double injectAEPower(ItemStack stack, double amount, Actionable mode) {
        final double maxStorage = this.getAEMaxPower(stack);
        final double currentStorage = this.getAECurrentPower(stack);
        final double required = maxStorage - currentStorage;
        //TODO this
        final double overflow = Math.max(0, Math.min(amount - required, amount));

        if (mode == Actionable.MODULATE) {
            var toAdd = Math.min(amount, required);
            setAECurrentPower(stack, currentStorage + toAdd);
        }

        return overflow;
    }

    @Override
    default double extractAEPower(ItemStack stack, double amount, Actionable mode) {
        final double currentStorage = this.getAECurrentPower(stack);
        final double fulfillable = Math.min(amount, currentStorage);

        if (mode == Actionable.MODULATE) {
            setAECurrentPower(stack, currentStorage - fulfillable);
        }

        return fulfillable;
    }

    @Override
    default double getAEMaxPower(ItemStack stack) {
        return AEConfig.instance().getWirelessTerminalBattery().getAsDouble();
    }

    @Override
    default double getAECurrentPower(ItemStack stack) {
        return stack.getOrDefault(AEComponents.STORED_ENERGY, 0.0);
    }

    default void setAECurrentPower(ItemStack stack, double power) {
        if (power < MIN_POWER) {
            stack.remove(AEComponents.STORED_ENERGY);
        } else {
            stack.set(AEComponents.STORED_ENERGY, power);
        }
    }

    @Override
    default AccessRestriction getPowerFlow(ItemStack stack) {
        return AccessRestriction.WRITE;
    }

    @Override
    default double getChargeRate(ItemStack stack) {
        return 800d;
    }
}
