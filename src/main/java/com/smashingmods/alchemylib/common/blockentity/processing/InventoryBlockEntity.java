package com.smashingmods.alchemylib.common.blockentity.processing;

import com.smashingmods.alchemylib.common.storage.AutomationSlotHandler;
import com.smashingmods.alchemylib.common.storage.ProcessingSlotHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public interface InventoryBlockEntity {

    ProcessingSlotHandler getInputHandler();

    ProcessingSlotHandler initializeInputHandler();

    ProcessingSlotHandler getOutputHandler();

    ProcessingSlotHandler initializeOutputHandler();

    AutomationSlotHandler getAutomationInputHandler(IItemHandlerModifiable pHandler);

    AutomationSlotHandler getAutomationOutputHandler(IItemHandlerModifiable pHandler);

    CombinedInvWrapper getAutomationInventory();
}
