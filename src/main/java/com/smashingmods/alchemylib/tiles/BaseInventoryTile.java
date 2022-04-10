package com.smashingmods.alchemylib.tiles;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

public abstract class BaseInventoryTile extends BaseTile implements InventoryTile {

    private CustomStackHandler input;
    private CustomStackHandler output;
    private AutomationStackHandler automationInput;
    private AutomationStackHandler automationOutput;
    private CombinedInvWrapper combinedInv;
    protected LazyOptional<IItemHandler> inventoryHolder = LazyOptional.of(() -> combinedInv);

    public BaseInventoryTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
        this.input = this.initInput();
        this.output = this.initOutput();
        this.automationInput = this.initAutomationInput(input);
        this.automationOutput = this.initAutomationOutput(output);
        combinedInv = new CombinedInvWrapper(automationInput, automationOutput);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        input.deserializeNBT(compound.getCompound("input"));
        output.deserializeNBT(compound.getCompound("output"));
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("input", input.serializeNBT());
        compound.put("output", output.serializeNBT());
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