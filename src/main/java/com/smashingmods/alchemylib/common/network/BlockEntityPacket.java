package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class BlockEntityPacket implements AlchemyPacket {

    public static final Type<BlockEntityPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "block_entity"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockEntityPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            ByteBufCodecs.COMPOUND_TAG, packet -> packet.tag,
            BlockEntityPacket::new
    );

    private final BlockPos blockPos;
    private final CompoundTag tag;

    public BlockEntityPacket(BlockPos pBlockPos, CompoundTag pTag) {
        this.blockPos = pBlockPos;
        this.tag = pTag;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        // Resolve the level from the payload context, not Minecraft.getInstance(): this packet is
        // client-bound, but referencing the client singleton would classload it on the dedicated server.
        var player = pContext.player();
        if (player.level().getBlockEntity(blockPos) instanceof AbstractProcessingBlockEntity blockEntity) {
            blockEntity.loadWithComponents(tag, player.registryAccess());
        }
    }
}
