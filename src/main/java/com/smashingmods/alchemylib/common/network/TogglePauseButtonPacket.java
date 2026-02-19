package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TogglePauseButtonPacket(BlockPos blockPos, boolean paused) implements CustomPacketPayload {
    public static final Type<TogglePauseButtonPacket> TYPE = new Type<>(AlchemyLib.modLoc("toggle_pause_button"));

    public static final StreamCodec<ByteBuf, TogglePauseButtonPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            TogglePauseButtonPacket::blockPos,

            ByteBufCodecs.BOOL,
            TogglePauseButtonPacket::paused,

            TogglePauseButtonPacket::new
    );

    public static void handle(TogglePauseButtonPacket packet, IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            Player player = pContext.player();

            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level().getBlockEntity(packet.blockPos);

            if (blockEntity != null) {
                blockEntity.setPaused(packet.paused);
                blockEntity.setChanged();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
