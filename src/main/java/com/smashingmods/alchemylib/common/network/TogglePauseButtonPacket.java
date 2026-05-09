package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TogglePauseButtonPacket(BlockPos blockPos, boolean paused) implements AlchemyPacket {

    public static final Type<TogglePauseButtonPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "toggle_pause_button"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TogglePauseButtonPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, TogglePauseButtonPacket::blockPos,
            ByteBufCodecs.BOOL, TogglePauseButtonPacket::paused,
            TogglePauseButtonPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            Player player = pContext.player();
            if (player != null && player.level().getBlockEntity(blockPos) instanceof AbstractProcessingBlockEntity blockEntity) {
                blockEntity.setPaused(paused);
                blockEntity.setChanged();
            }
        });
    }
}
