package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.common.network.SearchPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSearchableBlockEntity extends AbstractInventoryBlockEntity implements SearchableBlockEntity {

    private boolean recipeSelectorOpen = false;
    private String searchText = "";

    public AbstractSearchableBlockEntity(String pModId, BlockEntityType<?> pBlockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pModId, pBlockEntityType, pWorldPosition, pBlockState);
    }

    @Override
    public void setRecipeSelectorOpen(boolean pOpen) {
        this.recipeSelectorOpen = pOpen;
    }

    @Override
    public boolean isRecipeSelectorOpen() {
        return recipeSelectorOpen;
    }

    @Override
    public String getSearchText() {
        return searchText;
    }

    @Override
    public void setSearchText(@Nullable String pText) {
        if (pText != null && !pText.isEmpty()) {
            searchText = pText;
            if (level != null && level.isClientSide()) {
                PacketDistributor.sendToServer(new SearchPacket(getBlockPos(), searchText));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putString("searchText", searchText);
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        setSearchText(tag.getString("searchText"));
    }
}
