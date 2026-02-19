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

public record ToggleLockButtonPacket(BlockPos blockPos, boolean locked) implements CustomPacketPayload {
    public static final Type<ToggleLockButtonPacket> TYPE = new Type<>(AlchemyLib.modLoc("toggle_lock_button"));

    public static final StreamCodec<ByteBuf, ToggleLockButtonPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            ToggleLockButtonPacket::blockPos,

            ByteBufCodecs.BOOL,
            ToggleLockButtonPacket::locked,

            ToggleLockButtonPacket::new
    );
    
    public static void handle(ToggleLockButtonPacket packet, IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            Player player = pContext.player();
            AbstractProcessingBlockEntity blockEntity = (AbstractProcessingBlockEntity) player.level().getBlockEntity(packet.blockPos);

            if (blockEntity != null) {
                blockEntity.setRecipeLocked(packet.locked);
                blockEntity.setChanged();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
