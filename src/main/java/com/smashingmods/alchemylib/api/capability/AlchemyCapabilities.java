package com.smashingmods.alchemylib.api.capability;

import io.netty.util.internal.UnstableApi;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;

@SuppressWarnings("unused")
@UnstableApi
public class AlchemyCapabilities {
    public static Capability<HeatCapability> HEAT_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
}
