package com.nanaios.applied_ammo_box.item;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.core.localization.GuiText;
import appeng.core.localization.Tooltips;
import com.nanaios.applied_ammo_box.AppliedAmmoBoxLang;
import com.nanaios.applied_ammo_box.util.AE2LinkHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CreativeWirelessAmmoBoxItem extends WirelessAmmoBoxItem {

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, List<Component> tooltipComponents, @NotNull TooltipFlag tooltipFlag) {
        if (AE2LinkHelper.getLinkedPosition(stack) != null) {
            tooltipComponents.add(Tooltips.of(GuiText.Linked, Tooltips.GREEN));
        } else {
            tooltipComponents.add(Tooltips.of(GuiText.Unlinked, Tooltips.RED));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return AppliedAmmoBoxLang.CREATIVE_WIRELESS_AMMO_BOX_NAME.get().withStyle(ChatFormatting.DARK_PURPLE);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return false;
    }

    @Override
    public double extractAEPower(ItemStack stack, double amount, Actionable mode) {
        return amount;
    }

    @Override
    public double injectAEPower(ItemStack stack, double amount, Actionable mode) {
        return 0.0;
    }

    @Override
    public double getAEMaxPower(ItemStack stack) {
        return Double.MAX_VALUE;
    }

    @Override
    public double getAECurrentPower(ItemStack stack) {
        return Double.MAX_VALUE;
    }

    @Override
    public AccessRestriction getPowerFlow(ItemStack itemStack) {
        return AccessRestriction.NO_ACCESS;
    }
}