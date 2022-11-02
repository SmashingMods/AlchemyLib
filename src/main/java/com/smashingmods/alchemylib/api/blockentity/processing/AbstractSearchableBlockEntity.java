package com.smashingmods.alchemylib.api.blockentity.processing;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.network.SearchPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
                AlchemyLib.getPacketHandler().sendToServer(new SearchPacket(getBlockPos(), searchText));
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.putString("searchText", searchText);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        setSearchText(pTag.getString("searchText"));
    }
}
