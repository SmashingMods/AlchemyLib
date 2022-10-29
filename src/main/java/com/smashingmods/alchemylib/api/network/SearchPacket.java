package com.smashingmods.alchemylib.api.network;

import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class SearchPacket implements AlchemyPacket {

    private final BlockPos blockPos;
    private final String searchText;

    public SearchPacket(BlockPos pBlockPos, String pSearchText) {
        this.blockPos = pBlockPos;
        this.searchText = pSearchText;
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeUtf(searchText);
    }

    public static SearchPacket decode(FriendlyByteBuf pBuffer) {
        return new SearchPacket(pBuffer.readBlockPos(), pBuffer.readUtf());
    }

    @Override
    public void handle(NetworkEvent.Context pContext) {
        Player player = pContext.getSender();
        if (player != null) {
            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level.getBlockEntity(blockPos);

            if (blockEntity != null) {
                blockEntity.setSearchText(searchText);
                blockEntity.setChanged();
            }
        }
    }
}
