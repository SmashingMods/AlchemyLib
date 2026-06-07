package com.smashingmods.alchemylib.api.capability;

import com.smashingmods.alchemylib.api.blockentity.processing.AbstractFluidBlockEntity;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractInventoryBlockEntity;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import io.netty.util.internal.UnstableApi;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.function.BiFunction;

@SuppressWarnings("unused")
@UnstableApi
public class AlchemyCapabilities {

    /**
     * The heat capability token only; AlchemyLib registers no provider for it, so an unregistered query
     * resolves to {@code null}. Dependents that need heat register their own provider via {@link #register}.
     */
    public static final BlockCapability<HeatCapability, Direction> HEAT_HANDLER =
            BlockCapability.createSided(new ResourceLocation("alchemylib", "heat_handler"), HeatCapability.class);

    /**
     * Register a {@link BlockCapability} provider for a concrete {@link BlockEntityType}.
     *
     * <p>AlchemyLib's block entity bases are abstract and do not own the concrete
     * {@code BlockEntityType}s, so dependents must call this from their own
     * {@link RegisterCapabilitiesEvent} listener, keyed on their concrete types.
     *
     * @param event    the capability registration event
     * @param cap      the sided block capability to expose
     * @param type     the concrete block entity type to register the provider for
     * @param resolver resolves the capability instance for a block entity and side,
     *                 returning {@code null} when the capability is unavailable
     */
    public static <T, BE extends BlockEntity> void register(RegisterCapabilitiesEvent event,
            BlockCapability<T, Direction> cap, BlockEntityType<? extends BE> type,
            BiFunction<BE, Direction, T> resolver) {
        event.registerBlockEntity(cap, type, resolver::apply);
    }

    /**
     * Register the energy capability for an {@link AbstractProcessingBlockEntity} type, wired to
     * {@link AbstractProcessingBlockEntity#getEnergyHandler()}.
     */
    public static <BE extends AbstractProcessingBlockEntity> void registerEnergy(RegisterCapabilitiesEvent event,
            BlockEntityType<? extends BE> type) {
        register(event, Capabilities.EnergyStorage.BLOCK, type, (be, side) -> be.getEnergyHandler());
    }

    /**
     * Register the item handler capability for an {@link AbstractInventoryBlockEntity} type, wired to the
     * side-aware combined slot handler.
     */
    public static <BE extends AbstractInventoryBlockEntity> void registerInventoryItemHandler(RegisterCapabilitiesEvent event,
            BlockEntityType<? extends BE> type) {
        register(event, Capabilities.ItemHandler.BLOCK, type, (be, side) -> be.getCombinedSlotHandler().getView(side));
    }

    /**
     * Register the energy and item handler capabilities for an {@link AbstractInventoryBlockEntity} type.
     */
    public static <BE extends AbstractInventoryBlockEntity> void registerInventory(RegisterCapabilitiesEvent event,
            BlockEntityType<? extends BE> type) {
        registerEnergy(event, type);
        registerInventoryItemHandler(event, type);
    }

    /**
     * Register the energy, item handler and fluid handler capabilities for an
     * {@link AbstractFluidBlockEntity} type.
     */
    public static <BE extends AbstractFluidBlockEntity> void registerFluid(RegisterCapabilitiesEvent event,
            BlockEntityType<? extends BE> type) {
        registerEnergy(event, type);
        register(event, Capabilities.ItemHandler.BLOCK, type, (be, side) -> be.getCombinedSlotHandler().getView(side));
        register(event, Capabilities.FluidHandler.BLOCK, type, (be, side) -> be.getFluidStorage());
    }
}
