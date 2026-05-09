package com.smashingmods.alchemylib.api.capability;

import com.smashingmods.alchemylib.AlchemyLib;
import io.netty.util.internal.UnstableApi;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;

@SuppressWarnings("unused")
@UnstableApi
public class AlchemyCapabilities {
    public static final BlockCapability<HeatCapability, Direction> HEAT_HANDLER =
            BlockCapability.createSided(
                    ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "heat_handler"),
                    HeatCapability.class
            );
}
