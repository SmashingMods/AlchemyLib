package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.api.blockentity.processing.AbstractSearchableBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
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

    public SearchPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.searchText = pBuffer.readUtf();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeUtf(searchText);
    }

    @Override
    public void handle(NetworkEvent.Context pContext) {
        Player player = pContext.getSender();
        if (player != null) {
            AbstractSearchableBlockEntity blockEntity = (AbstractSearchableBlockEntity) player.level().getBlockEntity(blockPos);

            if (blockEntity != null) {
                blockEntity.setSearchText(searchText);
                blockEntity.setChanged();
            }
        }
    }
}
