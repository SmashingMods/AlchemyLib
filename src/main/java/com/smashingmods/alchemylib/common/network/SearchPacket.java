package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractSearchableBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SearchPacket(BlockPos blockPos, String searchText) implements AlchemyPacket {

    public static final Type<SearchPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AlchemyLib.MODID, "search"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SearchPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, SearchPacket::blockPos,
            ByteBufCodecs.STRING_UTF8, SearchPacket::searchText,
            SearchPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            Player player = pContext.player();
            if (player != null) {
                if (player.level().getBlockEntity(blockPos) instanceof AbstractSearchableBlockEntity blockEntity) {
                    blockEntity.setSearchText(searchText);
                    blockEntity.setChanged();
                }
            }
        });
    }
}
