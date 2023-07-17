package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.FluidStorageHandler;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemylib.api.storage.SidedProcessingSlotWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public abstract class AbstractFluidBlockEntity extends AbstractProcessingBlockEntity implements FluidBlockEntity, InventoryBlockEntity {

    private final FluidStorageHandler fluidStorage = initializeFluidStorage();
    private final LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.of(() -> fluidStorage);

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

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> pCapability, @Nullable Direction pDirection) {
        if (pCapability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return getCombinedSlotHandler().getViewLazily(pDirection).cast();
        } else if (pCapability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return lazyFluidHandler.cast();
        }
        return super.getCapability(pCapability, pDirection);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        combinedHandler.invalidate();
        lazyFluidHandler.invalidate();
    }

    @Override
    public boolean isRecipeLocked() {
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("input", inputHandler.serializeNBT());
        pTag.put("output", outputHandler.serializeNBT());
        pTag.put("fluid", fluidStorage.writeToNBT(new CompoundTag()));
        pTag.putShort("sides", combinedHandler.sideModesToShort());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        inputHandler.deserializeNBT(pTag.getCompound("input"));
        outputHandler.deserializeNBT(pTag.getCompound("output"));
        fluidStorage.readFromNBT(pTag.getCompound("fluid"));
        if (pTag.contains("sides")) {
            combinedHandler.setSideModesFromShort(pTag.getShort("sides"));
        } else {
            combinedHandler.setSideModesFromShort(SidedProcessingSlotWrapper.LEGACY_SIDES_CONFIGURATION);
        }
    }

    public boolean onBlockActivated(Level pLevel, BlockPos pBlockPos, Player pPlayer, InteractionHand pHand) {
        return FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, pBlockPos, null);
    }
}
