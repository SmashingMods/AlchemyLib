package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.EnergyStorageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("unused")
public abstract class AbstractProcessingBlockEntity extends BlockEntity implements ProcessingBlockEntity, EnergyBlockEntity, MenuProvider {

    private final Component name;
    private int energyPerTick = 0;
    private int maxProgress = 0;
    private int progress = 0;
    private boolean canProcess = false;
    private boolean recipeLocked = false;
    private boolean paused = false;
    private boolean ioScreenOpen = false;

    private final EnergyStorageHandler energyHandler = initializeEnergyStorage();

    public AbstractProcessingBlockEntity(String pModId, BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pBlockEntityType, pWorldPosition, pBlockState);
        this.name = Component.translatable(String.format("%s.container.%s", pModId, BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(getType())));
    }

    @Override
    public Component getDisplayName() {
        return name;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public void onDataPacket(Connection pConnection, ClientboundBlockEntityDataPacket pPacket, HolderLookup.Provider provider) {
        Objects.requireNonNull(pPacket.getTag());
        this.loadAdditional(pPacket.getTag(), provider);
        super.onDataPacket(pConnection, pPacket, provider);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void tick() {
        if (level != null && !level.isClientSide()) {
            if (!paused) {
                if (!recipeLocked) {
                    updateRecipe();
                }
                if (canProcess) {
                    processRecipe();
                }
            }
        }
    }

    @Override
    public boolean getCanProcess() {
        return canProcess;
    }

    @Override
    public void setCanProcess(boolean pCanProcess) {
        canProcess = pCanProcess;
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public void setMaxProgress(int pMaxProgress) {
        maxProgress = pMaxProgress;
    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public void setProgress(int pProgress) {
        this.progress = pProgress;
    }

    @Override
    public void incrementProgress() {
        this.progress++;
    }

    @Override
    public boolean isRecipeLocked() {
        return this.recipeLocked;
    }

    @Override
    public void setRecipeLocked(boolean pRecipeLocked) {
        this.recipeLocked = pRecipeLocked;
    }

    @Override
    public boolean isProcessingPaused() {
        return this.paused;
    }

    @Override
    public void setPaused(boolean pPaused) {
        this.paused = pPaused;
    }

    @Override
    public boolean isSideConfigScreenOpen() {
        return ioScreenOpen;
    }

    @Override
    public void setSideConfigScreenState(boolean pState) {
        this.ioScreenOpen = pState;
    }

    @Override
    public EnergyStorageHandler getEnergyHandler() {
        return energyHandler;
    }

    /**
     * Returns the energy storage exposed for the given side, or null if none.
     * Used by capability registration via {@link net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage#BLOCK}.
     */
    public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
        return energyHandler;
    }

    public int getEnergyPerTick() {
        return energyPerTick;
    }

    public void setEnergyPerTick(int pEnergyPerTick) {
        energyPerTick = pEnergyPerTick;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider provider) {
        pTag.putInt("progress", progress);
        pTag.putBoolean("locked", isRecipeLocked());
        pTag.putBoolean("paused", isProcessingPaused());
        pTag.putInt("energy", energyHandler.getEnergyStored());
        super.saveAdditional(pTag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider provider) {
        super.loadAdditional(pTag, provider);
        setProgress(pTag.getInt("progress"));
        setRecipeLocked(pTag.getBoolean("locked"));
        setPaused(pTag.getBoolean("paused"));
        if (pTag.contains("energy")) {
            energyHandler.setEnergy(pTag.getInt("energy"));
        }
    }
}
