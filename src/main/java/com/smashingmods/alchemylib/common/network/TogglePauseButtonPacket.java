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
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class TogglePauseButtonPacket implements AlchemyPacket {

    public static final Type<TogglePauseButtonPacket> TYPE = new Type<>(new ResourceLocation(AlchemyLib.MODID, "toggle_pause_button"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TogglePauseButtonPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            ByteBufCodecs.BOOL, packet -> packet.paused,
            TogglePauseButtonPacket::new
    );

    private final BlockPos blockPos;
    private final boolean paused;

    public TogglePauseButtonPacket(BlockPos pBlockPos, boolean pPause) {
        this.blockPos = pBlockPos;
        this.paused = pPause;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        if (pContext.player().level().getBlockEntity(blockPos) instanceof AbstractProcessingBlockEntity blockEntity) {
            blockEntity.setPaused(paused);
            blockEntity.setChanged();
        }
    }
}
