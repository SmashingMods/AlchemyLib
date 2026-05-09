package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import com.smashingmods.alchemylib.api.storage.SidedProcessingSlotWrapper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

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
        if (!inputHandler.isEmpty()) {
            setCanProcess(canProcessRecipe());
        }
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

    /**
     * Returns the item handler exposed for the given side. Used by capability registration via
     * {@link net.neoforged.neoforge.capabilities.Capabilities.ItemHandler#BLOCK}.
     */
    public IItemHandler getItemHandler(@Nullable Direction side) {
        return combinedHandler.getView(side);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider provider) {
        pTag.put("input", inputHandler.serializeNBT(provider));
        pTag.put("output", outputHandler.serializeNBT(provider));
        pTag.putShort("sides", combinedHandler.sideModesToShort());
        super.saveAdditional(pTag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider provider) {
        super.loadAdditional(pTag, provider);
        inputHandler.deserializeNBT(provider, pTag.getCompound("input"));
        outputHandler.deserializeNBT(provider, pTag.getCompound("output"));
        if (pTag.contains("sides")) {
            combinedHandler.setSideModesFromShort(pTag.getShort("sides"));
        } else {
            combinedHandler.setSideModesFromShort(SidedProcessingSlotWrapper.LEGACY_SIDES_CONFIGURATION);
        }
    }
}
