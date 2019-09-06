package al132.alib.tiles;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public abstract class ABaseInventoryTile extends ABaseTile implements InventoryTile {

    private CustomStackHandler input;
    private CustomStackHandler output;
    private AutomationStackHandler automationInput;
    private AutomationStackHandler automationOutput;
    private CombinedInvWrapper combinedInv;
    protected LazyOptional<IItemHandler> inventoryHolder = LazyOptional.of(() -> combinedInv);

    public ABaseInventoryTile(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        this.input = this.initInput();
        this.output = this.initOutput();
        this.automationInput = this.initAutomationInput(input);
        this.automationOutput = this.initAutomationOutput(output);
        combinedInv = new CombinedInvWrapper(automationInput, automationOutput);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        input.deserializeNBT(compound.getCompound("input"));
        output.deserializeNBT(compound.getCompound("output"));
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("input", input.serializeNBT());
        compound.put("output", output.serializeNBT());
        return super.write(compound);
    }

    @Override
    public LazyOptional<IItemHandler> getExternalInventory() {
        return inventoryHolder;
    }

    @Override
    public CombinedInvWrapper getAutomationInventory() {
        return combinedInv;
    }

    @Override
    public CustomStackHandler getInput() {
        return input;
    }

    @Override
    public CustomStackHandler getOutput() {
        return output;
    }
}