package com.smashingmods.alchemylib.api.capability;

import com.smashingmods.alchemylib.AlchemyLib;
import io.netty.util.internal.UnstableApi;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@UnstableApi
public class AlchemyCapabilities {
    // TODO
    public static final BlockCapability<HeatCapability, @Nullable Direction> HEAT_HANDLER = BlockCapability.createSided(AlchemyLib.modLoc("heat"), HeatCapability.class);
}
