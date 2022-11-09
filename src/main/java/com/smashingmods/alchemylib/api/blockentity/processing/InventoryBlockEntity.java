package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

/**
 * Attach optional input and output handler inventories to a {@link AbstractProcessingBlockEntity}.
 *
 * <p>Implementers can use
 * one or both input and output handlers. Unused handlers must still be initialized to prevent nullness. Return a new
 * {@link ProcessingSlotHandler} with a size of 0 to disable it</p>
 */
@SuppressWarnings("unused")
public interface InventoryBlockEntity {

    /**
     * Initialize and return the input handler for this block entity.
     *
     * @return {@link ProcessingSlotHandler}
     */
    ProcessingSlotHandler initializeInputHandler();

    /**
     *  Initialize and return the output handler for this block entity.
     *
     * @return {@link ProcessingSlotHandler}
     */
    ProcessingSlotHandler initializeOutputHandler();

    /**
     * @return {@link ProcessingSlotHandler}
     */
    ProcessingSlotHandler getInputHandler();

    /**
     * @return {@link ProcessingSlotHandler}
     */
    ProcessingSlotHandler getOutputHandler();

    /**
     * Implementers will need to create a CombinedInvWrapper field of the input and output handlers. This makes automation
     * easier by returning the combined inventory in {@link net.minecraftforge.common.extensions.IForgeBlockEntity#getCapability(Capability, Direction) IForgeBlockEntity#getCapability(Capability, Direction)}.
     *
     * @return {@link CombinedInvWrapper}
     *
     * @see AbstractInventoryBlockEntity#lazyItemHandler
     * @see AbstractInventoryBlockEntity#getCapability(Capability, Direction)
     */
    @SuppressWarnings("JavadocReference")
    CombinedInvWrapper getCombinedInvWrapper();

    /**
     * When the block entity is broken, drop the contents held in all the handlers into the world so that the
     * items aren't voided / deleted. You can override this for different behavior or to add other inventories
     * to be dropped.
     *
     * @param pLevel Level where the block entity will drop its contents. Theoretically you could set this
     *               to a random place in the end, but why would you do that, you monster!
     * @param pPos Position in the level to drop contents.
     */
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
