package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class TogglePauseButtonPacket implements AlchemyPacket {

    private final BlockPos blockPos;
    private final boolean paused;

    public TogglePauseButtonPacket(BlockPos pBlockPos, boolean pPause) {
        this.blockPos = pBlockPos;
        this.paused = pPause;
    }

    public TogglePauseButtonPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.paused = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(paused);
    }

    public void handle(NetworkEvent.Context pContext) {
        Player player = pContext.getSender();
        if (player != null) {
            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level.getBlockEntity(blockPos);

            if (blockEntity != null) {
                blockEntity.setPaused(paused);
                blockEntity.setChanged();
            }
        }
    }
}
