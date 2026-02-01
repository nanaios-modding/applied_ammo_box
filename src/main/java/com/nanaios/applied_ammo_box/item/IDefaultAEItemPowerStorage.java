package com.nanaios.applied_ammo_box.item;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.core.AEConfig;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public interface IDefaultAEItemPowerStorage extends IAEItemPowerStorage {
    double MIN_POWER = 0.0001;
    String CURRENT_POWER_NBT_KEY = "internalCurrentPower";
    String MAX_POWER_NBT_KEY = "internalMaxPower";

    @Override
    default double injectAEPower(ItemStack stack, double amount, Actionable mode) {
        final double maxStorage = this.getAEMaxPower(stack);
        final double currentStorage = this.getAECurrentPower(stack);
        final double required = maxStorage - currentStorage;
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
        // Allow per-item-stack overrides of the maximum power storage
        var tag = stack.getTag();
        if (tag != null && tag.contains(MAX_POWER_NBT_KEY, Tag.TAG_DOUBLE)) {
            return tag.getDouble(MAX_POWER_NBT_KEY);
        }

        return AEConfig.instance().getWirelessTerminalBattery().getAsDouble();
    }

    default void setAEMaxPower(ItemStack stack, double maxPower) {
        var defaultCapacity = AEConfig.instance().getWirelessTerminalBattery().getAsDouble();
        if (Math.abs(maxPower - defaultCapacity) < MIN_POWER) {
            stack.removeTagKey(MAX_POWER_NBT_KEY);
            maxPower = defaultCapacity;
        } else {
            stack.getOrCreateTag().putDouble(MAX_POWER_NBT_KEY, maxPower);
        }

        // Clamp current power to be within bounds
        var currentPower = getAECurrentPower(stack);
        if (currentPower > maxPower) {
            setAECurrentPower(stack, maxPower);
        }
    }

    default void setAEMaxPowerMultiplier(ItemStack stack, int multiplier) {
        multiplier = Mth.clamp(multiplier, 1, 100);
        setAEMaxPower(stack, multiplier * AEConfig.instance().getWirelessTerminalBattery().getAsDouble());
    }

    default void resetAEMaxPower(ItemStack stack) {
        setAEMaxPower(stack, AEConfig.instance().getWirelessTerminalBattery().getAsDouble());
    }

    @Override
    default double getAECurrentPower(ItemStack is) {
        var tag = is.getTag();
        if (tag != null) {
            return tag.getDouble(CURRENT_POWER_NBT_KEY);
        } else {
            return 0;
        }
    }

    default void setAECurrentPower(ItemStack stack, double power) {
        if (power < MIN_POWER) {
            stack.removeTagKey(CURRENT_POWER_NBT_KEY);
        } else {
            stack.getOrCreateTag().putDouble(CURRENT_POWER_NBT_KEY, power);
        }
    }

    @Override
    default AccessRestriction getPowerFlow(ItemStack is) {
        return AccessRestriction.WRITE;
    }

    @Override
    default double getChargeRate(ItemStack stack) {
        return 800d;
    }
}
