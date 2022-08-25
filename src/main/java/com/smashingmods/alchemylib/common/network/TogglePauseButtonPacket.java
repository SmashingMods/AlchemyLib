package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.common.blockentity.processing.AbstractProcessingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class TogglePauseButtonPacket {

    private final BlockPos blockPos;
    private final boolean pause;

    public TogglePauseButtonPacket(BlockPos pBlockPos, boolean pPause) {
        this.blockPos = pBlockPos;
        this.pause = pPause;
    }

    public TogglePauseButtonPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.pause = pBuffer.readBoolean();
    }

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(pause);
    }

    public static void handle(final TogglePauseButtonPacket pPacket, Supplier<NetworkEvent.Context> pContext) {
        pContext.get().enqueueWork(() -> {
            Player player = pContext.get().getSender();
            Objects.requireNonNull(player);

            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level.getBlockEntity(pPacket.blockPos);

            Objects.requireNonNull(blockEntity);

            blockEntity.setPaused(pPacket.pause);
            blockEntity.setChanged();
        });
        pContext.get().setPacketHandled(true);
    }
}
