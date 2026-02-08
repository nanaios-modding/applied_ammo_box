package com.nanaios.applied_ammo_box.capabilitys;

import appeng.api.config.Actionable;
import appeng.api.config.PowerUnits;
import appeng.api.implementations.items.IAEItemPowerStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record WirelessAmmoBoxCapabilityProvider(ItemStack stack, IAEItemPowerStorage item) implements ICapabilityProvider,IEnergyStorage {
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return LazyOptional.of(() -> this).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        final double convertedOffer = PowerUnits.FE.convertTo(PowerUnits.AE, maxReceive);
        final double overflow = item.injectAEPower(stack, convertedOffer,
                simulate ? Actionable.SIMULATE : Actionable.MODULATE);

        return maxReceive - (int) PowerUnits.AE.convertTo(PowerUnits.FE, overflow);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return (int) PowerUnits.AE.convertTo(PowerUnits.FE, item.getAECurrentPower(stack));
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) PowerUnits.AE.convertTo(PowerUnits.FE, item.getAEMaxPower(stack));
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }
}
