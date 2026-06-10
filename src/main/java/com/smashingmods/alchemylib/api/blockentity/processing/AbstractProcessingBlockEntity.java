package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.storage.EnergyStorageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
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
        this.name = MutableComponent.create(new TranslatableContents(String.format("%s.container.%s", pModId, BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(getType())), null, TranslatableContents.NO_ARGS));
    }

    @Override
    public Component getDisplayName() {
        return name;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = super.getUpdateTag(pRegistries);
        saveAdditional(tag, pRegistries);
        return tag;
    }

    @Override
    public void onDataPacket(Connection pConnection, ClientboundBlockEntityDataPacket pPacket, HolderLookup.Provider pRegistries) {
        Objects.requireNonNull(pPacket.getTag());
        this.loadAdditional(pPacket.getTag(), pRegistries);
        super.onDataPacket(pConnection, pPacket, pRegistries);
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

    /**
     * 1.21.5 split the old {@code BlockBehaviour#onRemove} removal logic in two: dropping anything held by the block
     * entity moved to {@link BlockEntity#preRemoveSideEffects(BlockPos, BlockState)} (called by {@code LevelChunk}
     * while the block entity is still in the level, before it is removed), and neighbour updates moved to
     * {@code BlockBehaviour#affectNeighborsAfterRemoval} (which runs after the block entity is gone). The inventory
     * drop therefore belongs here rather than on {@link com.smashingmods.alchemylib.api.block.AbstractProcessingBlock}.
     *
     * <p>The vanilla default only drops block entities that implement {@link net.minecraft.world.Container}; these
     * machines hold their inventory in an {@code IItemHandler}, so {@link InventoryBlockEntity#dropContents(Level, BlockPos)}
     * is invoked for any subclass mixing in {@link InventoryBlockEntity}. {@code super} is still called so the vanilla
     * behaviour applies to any subclass that also implements {@link net.minecraft.world.Container}.
     */
    @Override
    public void preRemoveSideEffects(BlockPos pPos, BlockState pState) {
        if (level != null && this instanceof InventoryBlockEntity inventoryBlockEntity) {
            inventoryBlockEntity.dropContents(level, pPos);
        }
        super.preRemoveSideEffects(pPos, pState);
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

    public int getEnergyPerTick() {
        return energyPerTick;
    }

    public void setEnergyPerTick(int pEnergyPerTick) {
        energyPerTick = pEnergyPerTick;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        pTag.putInt("progress", progress);
        pTag.putBoolean("locked", isRecipeLocked());
        pTag.putBoolean("paused", isProcessingPaused());
        pTag.put("energy", energyHandler.serializeNBT(pRegistries));
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        setProgress(pTag.getIntOr("progress", 0));
        setRecipeLocked(pTag.getBooleanOr("locked", false));
        setPaused(pTag.getBooleanOr("paused", false));
        if (pTag.contains("energy")) {
            energyHandler.deserializeNBT(pRegistries, pTag.get("energy"));
        }
    }
}
