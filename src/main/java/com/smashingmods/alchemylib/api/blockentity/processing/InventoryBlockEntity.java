package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

@SuppressWarnings("unused")
public interface InventoryBlockEntity {

    ProcessingSlotHandler getInputHandler();

    ProcessingSlotHandler initializeInputHandler();

    ProcessingSlotHandler getOutputHandler();

    ProcessingSlotHandler initializeOutputHandler();

    CombinedInvWrapper getCombinedInvWrapper();

    default void dropContents(Level pLevel, BlockPos pPos) {
        if (!pLevel.isClientSide()) {
            SimpleContainer container = new SimpleContainer(getCombinedInvWrapper().getSlots());
            for (int i = 0; i < getCombinedInvWrapper().getSlots(); i++) {
                container.setItem(i, getCombinedInvWrapper().getStackInSlot(i));
            }
            Containers.dropContents(pLevel, pPos, container);
        }
    }
}
