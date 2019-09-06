package al132.alib.tiles;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public interface InventoryTile {

    CustomStackHandler initInput();

    CustomStackHandler initOutput();

    AutomationStackHandler initAutomationInput(IItemHandlerModifiable inv);

    AutomationStackHandler initAutomationOutput(IItemHandlerModifiable inv);

    CustomStackHandler getInput();

    CustomStackHandler getOutput();

    LazyOptional<IItemHandler> getExternalInventory();

    CombinedInvWrapper getAutomationInventory();
}