package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.AutomationSlotHandler;
import com.smashingmods.alchemylib.api.storage.ProcessingSlotHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

@SuppressWarnings("unused")
public interface InventoryBlockEntity {

    ProcessingSlotHandler getInputHandler();

    ProcessingSlotHandler initializeInputHandler();

    ProcessingSlotHandler getOutputHandler();

    ProcessingSlotHandler initializeOutputHandler();

    AutomationSlotHandler getAutomationInputHandler(IItemHandlerModifiable pHandler);

    AutomationSlotHandler getAutomationOutputHandler(IItemHandlerModifiable pHandler);

    CombinedInvWrapper getAutomationInventory();
}
