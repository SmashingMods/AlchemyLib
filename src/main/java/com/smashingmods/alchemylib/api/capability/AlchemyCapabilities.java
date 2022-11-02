package com.smashingmods.alchemylib.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

@SuppressWarnings("unused")
public class AlchemyCapabilities {
    public static Capability<HeatCapability> HEAT_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
}
