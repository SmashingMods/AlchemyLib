package com.smashingmods.alchemylib.api.capability;

import io.netty.util.internal.UnstableApi;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

@SuppressWarnings("unused")
@UnstableApi
public class AlchemyCapabilities {
    public static Capability<HeatCapability> HEAT_HANDLER = CapabilityManager.get(new CapabilityToken<>() {});
}
