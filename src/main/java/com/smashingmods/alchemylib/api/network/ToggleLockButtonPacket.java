package com.smashingmods.alchemylib.api.network;

import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
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

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(locked);
    }

    public static ToggleLockButtonPacket decode(FriendlyByteBuf pBuffer) {
        return new ToggleLockButtonPacket(pBuffer.readBlockPos(), pBuffer.readBoolean());
    }

    public void handle(NetworkEvent.Context pContext) {
        Player player = pContext.getSender();
        if (player != null) {
            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level.getBlockEntity(blockPos);

            if (blockEntity != null) {
                blockEntity.setRecipeLocked(locked);
                blockEntity.setChanged();
            }
        }
    }
}
