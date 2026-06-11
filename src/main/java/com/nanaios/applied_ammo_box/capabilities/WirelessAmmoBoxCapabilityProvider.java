package com.nanaios.applied_ammo_box.capabilities;

import appeng.api.config.Actionable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record WirelessAmmoBoxCapabilityProvider(ItemStack stack,
                                                IAEItemPowerStorage item) implements ICapabilityProvider, IEnergyStorage {
    @Override
    public @Nullable Object getCapability(Object object, Object context) {
        return null;
    }

    @Override
    public int receiveEnergy(int toReceive, boolean simulate) {
        return 0;
    }

    @Override
    public int extractEnergy(int toExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return false;
    }
    /*@Override
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
    }*/
}
