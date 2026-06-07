package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractSearchableBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class SearchPacket implements AlchemyPacket {

    public static final ResourceLocation ID = new ResourceLocation(AlchemyLib.MODID, "search");

    private final BlockPos blockPos;
    private final String searchText;

    public SearchPacket(BlockPos pBlockPos, String pSearchText) {
        this.blockPos = pBlockPos;
        this.searchText = pSearchText;
    }

    public SearchPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.searchText = pBuffer.readUtf();
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeUtf(searchText);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handle(PlayPayloadContext pContext) {
        pContext.player().ifPresent(player -> {
            if (player.level().getBlockEntity(blockPos) instanceof AbstractSearchableBlockEntity blockEntity) {
                blockEntity.setSearchText(searchText);
                blockEntity.setChanged();
            }
        });
    }
}
