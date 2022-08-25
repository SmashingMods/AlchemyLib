package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.common.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class ToggleLockButtonPacket {

    private final BlockPos blockPos;
    private final boolean lock;

    public ToggleLockButtonPacket(BlockPos pBlockPos, boolean pLock) {
        this.blockPos = pBlockPos;
        this.lock = pLock;
    }

    public ToggleLockButtonPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.lock = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(lock);
    }

    public static void handle(final ToggleLockButtonPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            Player player = pContext.get().getSender();
            Objects.requireNonNull(player);

            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level.getBlockEntity(pPacket.blockPos);

            Objects.requireNonNull(blockEntity);

            blockEntity.setRecipeLocked(pPacket.lock);
            blockEntity.setChanged();
        });
        pContext.get().setPacketHandled(true);
    }
}
