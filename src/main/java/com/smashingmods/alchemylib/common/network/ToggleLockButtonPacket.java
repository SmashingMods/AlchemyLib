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

public class ToggleLockButtonPacket implements AlchemyPacket {

    public static final Type<ToggleLockButtonPacket> TYPE = new Type<>(new ResourceLocation(AlchemyLib.MODID, "toggle_lock_button"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleLockButtonPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            ByteBufCodecs.BOOL, packet -> packet.locked,
            ToggleLockButtonPacket::new
    );

    private final BlockPos blockPos;
    private final boolean locked;

    public ToggleLockButtonPacket(BlockPos pBlockPos, boolean pLock) {
        this.blockPos = pBlockPos;
        this.locked = pLock;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        if (pContext.player().level().getBlockEntity(blockPos) instanceof AbstractProcessingBlockEntity blockEntity) {
            blockEntity.setRecipeLocked(locked);
            blockEntity.setChanged();
        }
    }
}
