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
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public abstract class AbstractFluidBlockEntity extends AbstractProcessingBlockEntity implements FluidBlockEntity, InventoryBlockEntity {

    private final FluidStorageHandler fluidStorage = initializeFluidStorage();

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

    /**
     * Returns the item handler exposed for the given side. Used by capability registration via
     * {@link net.neoforged.neoforge.capabilities.Capabilities.ItemHandler#BLOCK}.
     */
    public IItemHandler getItemHandler(@Nullable Direction side) {
        return combinedHandler.getView(side);
    }

    /**
     * Returns the fluid handler exposed for the given side. Used by capability registration via
     * {@link net.neoforged.neoforge.capabilities.Capabilities.FluidHandler#BLOCK}.
     */
    public IFluidHandler getFluidHandler(@Nullable Direction side) {
        return fluidStorage;
    }

    @Override
    public boolean isRecipeLocked() {
        return false;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider provider) {
        pTag.put("input", inputHandler.serializeNBT(provider));
        pTag.put("output", outputHandler.serializeNBT(provider));
        pTag.put("fluid", fluidStorage.writeToNBT(provider, new CompoundTag()));
        pTag.putShort("sides", combinedHandler.sideModesToShort());
        super.saveAdditional(pTag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider provider) {
        super.loadAdditional(pTag, provider);
        inputHandler.deserializeNBT(provider, pTag.getCompound("input"));
        outputHandler.deserializeNBT(provider, pTag.getCompound("output"));
        fluidStorage.readFromNBT(provider, pTag.getCompound("fluid"));
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
