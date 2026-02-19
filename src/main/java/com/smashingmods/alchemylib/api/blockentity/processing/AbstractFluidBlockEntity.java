package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.FluidStorageHandler;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemylib.api.storage.SidedProcessingSlotWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public abstract class AbstractFluidBlockEntity extends AbstractProcessingBlockEntity implements FluidBlockEntity, InventoryBlockEntity {

    private final FluidStorageHandler fluidStorage = initializeFluidStorage();
    private final Lazy<IFluidHandler> lazyFluidHandler = Lazy.of(() -> fluidStorage);

    private final ProcessingSlotHandler inputHandler = initializeInputHandler();
    private final ProcessingSlotHandler outputHandler = initializeOutputHandler();
    private final SidedProcessingSlotWrapper combinedHandler = new SidedProcessingSlotWrapper(inputHandler, outputHandler);

    public AbstractFluidBlockEntity(String pModId, BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pModId, pBlockEntityType, pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        if (!getFluidStorage().isEmpty()) {
            setCanProcess(canProcessRecipe());
        }
        super.tick();
    }

    @Override
    public FluidStorageHandler getFluidStorage() {
        return fluidStorage;
    }

    @Override
    public ProcessingSlotHandler getInputHandler() {
        return inputHandler;
    }

    @Override
    public ProcessingSlotHandler getOutputHandler() {
        return outputHandler;
    }

    @Override
    public SidedProcessingSlotWrapper getCombinedSlotHandler() {
        return combinedHandler;
    }

    /* TODO
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> pCapability, @Nullable Direction pDirection) {
        if (pCapability == ForgeCapabilities.ITEM_HANDLER) {
            return getCombinedSlotHandler().getViewLazily(pDirection).cast();
        } else if (pCapability == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }
        return super.getCapability(pCapability, pDirection);
    }
     */
    @Override
    public void invalidateCapabilities() {
        combinedHandler.invalidate();
        lazyFluidHandler.invalidate();
        super.invalidateCapabilities();
    }

    @Override
    public boolean isRecipeLocked() {
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("input", inputHandler.serializeNBT(registries));
        tag.put("output", outputHandler.serializeNBT(registries));
        tag.put("fluid", fluidStorage.writeToNBT(registries, new CompoundTag()));
        tag.putShort("sides", combinedHandler.sideModesToShort());
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inputHandler.deserializeNBT(registries, tag.getCompound("input"));
        outputHandler.deserializeNBT(registries, tag.getCompound("output"));
        fluidStorage.readFromNBT(registries, tag.getCompound("fluid"));
        if (tag.contains("sides")) {
            combinedHandler.setSideModesFromShort(tag.getShort("sides"));
        } else {
            combinedHandler.setSideModesFromShort(SidedProcessingSlotWrapper.LEGACY_SIDES_CONFIGURATION);
        }
    }

    public boolean onBlockActivated(Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand) {
        return FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, pBlockPos, null);
    }
}
