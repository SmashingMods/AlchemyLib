package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.api.recipe.AbstractProcessingRecipe;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
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

    // The id of the recipe the player explicitly chose in the recipe selector, or null when the machine
    // auto-picks. Tracked separately from the current recipe so updateRecipe can tell a player's choice
    // apart from its own tick-time auto-pick and keep the choice while it still matches the inputs.
    @Nullable
    private ResourceLocation selectedRecipeId = null;

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

    /**
     * Applies a player's recipe-selector choice to this machine. This is the single server-side entry point
     * for recipe selection: the network handler delegates here, and tests can call it to drive the real
     * selection path. The choice is remembered (see {@link #getSelectedRecipeId()}) so a machine's
     * {@link #updateRecipe()} can keep it over the first-in-sort-order auto-pick while the inputs still
     * match it. Re-selecting the recipe that is already both current and selected is a no-op, so repeated
     * clicks or client re-sends never reset progress mid-operation. A locked machine refuses the selection
     * outright -- the lock's contract is that the recipe can't be changed.
     */
    public <R extends AbstractProcessingRecipe> void selectRecipe(R pRecipe) {
        if (isRecipeLocked()) {
            return;
        }
        AbstractProcessingRecipe currentRecipe = getRecipe();
        if (currentRecipe != null && currentRecipe.equals(pRecipe) && pRecipe.getId().equals(selectedRecipeId)) {
            return;
        }
        setProgress(0);
        setRecipe(pRecipe);
        selectedRecipeId = pRecipe.getId();
        setChanged();
    }

    /**
     * The id of the recipe the player explicitly selected, or null when the machine auto-picks. Selections
     * persist until the player picks another recipe; inputs coming and going do not clear them.
     */
    @Nullable
    public ResourceLocation getSelectedRecipeId() {
        return selectedRecipeId;
    }

    /**
     * Whether the given recipe is the player's explicit selection. Intended for {@link #updateRecipe()}
     * predicates: prefer a matching recipe this returns true for before falling back to the first match.
     */
    public boolean isSelectedRecipe(AbstractProcessingRecipe pRecipe) {
        return selectedRecipeId != null && selectedRecipeId.equals(pRecipe.getId());
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
        if (selectedRecipeId != null) {
            pTag.putString("selectedRecipe", selectedRecipeId.toString());
        }
        pTag.put("energy", energyHandler.serializeNBT(pRegistries));
        super.saveAdditional(pTag, pRegistries);
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        setProgress(pTag.getInt("progress"));
        setRecipeLocked(pTag.getBoolean("locked"));
        setPaused(pTag.getBoolean("paused"));
        // Reset to null when the key is absent: this tag also arrives through the menu's client sync, so a
        // cleared selection on the server must clear the client mirror too.
        selectedRecipeId = pTag.contains("selectedRecipe") ? ResourceLocation.tryParse(pTag.getString("selectedRecipe")) : null;
        if (pTag.contains("energy")) {
            energyHandler.deserializeNBT(pRegistries, pTag.get("energy"));
        }
    }
}
