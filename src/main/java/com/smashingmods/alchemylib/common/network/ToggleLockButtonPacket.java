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

public record ToggleLockButtonPacket(BlockPos blockPos, boolean locked) implements AlchemyPacket {

    public static final Type<ToggleLockButtonPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "toggle_lock_button"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ToggleLockButtonPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ToggleLockButtonPacket::blockPos,
            ByteBufCodecs.BOOL, ToggleLockButtonPacket::locked,
            ToggleLockButtonPacket::new
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
                blockEntity.setRecipeLocked(locked);
                blockEntity.setChanged();
            }
        });
    }
}
