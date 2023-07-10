package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

public class ToggleLockButtonPacket implements AlchemyPacket {

    private final BlockPos blockPos;
    private final boolean locked;

    public ToggleLockButtonPacket(BlockPos pBlockPos, boolean pLock) {
        this.blockPos = pBlockPos;
        this.locked = pLock;
    }

    public ToggleLockButtonPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.locked = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(locked);
    }

    public void handle(NetworkEvent.Context pContext) {
        Player player = pContext.getSender();
        if (player != null) {
            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level().getBlockEntity(blockPos);

            if (blockEntity != null) {
                blockEntity.setRecipeLocked(locked);
                blockEntity.setChanged();
            }
        }
    }
}
