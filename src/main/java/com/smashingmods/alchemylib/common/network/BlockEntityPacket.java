package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

public record BlockEntityPacket(BlockPos blockPos, CompoundTag tag) implements CustomPacketPayload {
    public static final Type<BlockEntityPacket> TYPE = new Type<>(AlchemyLib.modLoc("block_entity"));

    public static final StreamCodec<ByteBuf, BlockEntityPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            BlockEntityPacket::blockPos,

            ByteBufCodecs.COMPOUND_TAG,
            BlockEntityPacket::tag,

            BlockEntityPacket::new
    );

    public static void handle(BlockEntityPacket packet, IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            BlockEntity blockEntity = Objects.requireNonNull(level).getBlockEntity(packet.blockPos);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
