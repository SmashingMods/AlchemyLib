package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record BlockEntityPacket(BlockPos blockPos, CompoundTag tag) implements AlchemyPacket {

    public static final Type<BlockEntityPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "block_entity_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockEntityPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BlockEntityPacket::blockPos,
            ByteBufCodecs.COMPOUND_TAG, BlockEntityPacket::tag,
            BlockEntityPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            Level level = pContext.player().level();
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (blockEntity != null) {
                blockEntity.loadWithComponents(tag, level.registryAccess());
            }
        });
    }
}
