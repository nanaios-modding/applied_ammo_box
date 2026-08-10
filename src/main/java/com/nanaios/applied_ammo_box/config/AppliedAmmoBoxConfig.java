package com.nanaios.applied_ammo_box.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AppliedAmmoBoxConfig {
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    
	public static final ModConfigSpec.DoubleValue AMMO_BOX_PER_ROUND_POWER_USAGE = BUILDER
			.comment("How much power the ammo box uses per round")
			.defineInRange("AmmoBoxPerRoundPowerUsage", 1000.0, 0.0, Double.MAX_VALUE);

	public static final ModConfigSpec SPEC = BUILDER.build();

}
