package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemylib.api.storage.SidedProcessingSlotWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("unused")
public abstract class AbstractInventoryBlockEntity extends AbstractProcessingBlockEntity implements InventoryBlockEntity {

    private final ProcessingSlotHandler inputHandler = initializeInputHandler();
    private final ProcessingSlotHandler outputHandler = initializeOutputHandler();
    private final SidedProcessingSlotWrapper combinedHandler = new SidedProcessingSlotWrapper(inputHandler, outputHandler);

    public AbstractInventoryBlockEntity(String pModId, BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pModId, pBlockEntityType, pWorldPosition, pBlockState);
    }

    @Override
    public void tick() {
        // Defense-in-depth: recompute unconditionally so base correctness doesn't rely on every subclass refreshing canProcess in its input handler's onContentsChanged.
        setCanProcess(canProcessRecipe());
        super.tick();
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

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.put("input", inputHandler.serializeNBT(pRegistries));
        pTag.put("output", outputHandler.serializeNBT(pRegistries));
        pTag.putShort("sides", combinedHandler.sideModesToShort());
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        inputHandler.deserializeNBT(pRegistries, pTag.getCompoundOrEmpty("input"));
        outputHandler.deserializeNBT(pRegistries, pTag.getCompoundOrEmpty("output"));
        if (pTag.contains("sides")) {
            combinedHandler.setSideModesFromShort(pTag.getShortOr("sides", (short) 0));
        } else {
            combinedHandler.setSideModesFromShort(SidedProcessingSlotWrapper.LEGACY_SIDES_CONFIGURATION);
        }
    }
}
