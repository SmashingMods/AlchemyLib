package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractSearchableBlockEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SearchPacket(BlockPos blockPos, String searchText) implements CustomPacketPayload {
    public static final Type<SearchPacket> TYPE = new Type<>(AlchemyLib.modLoc("search"));

    public static final StreamCodec<ByteBuf, SearchPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            SearchPacket::blockPos,

            ByteBufCodecs.STRING_UTF8,
            SearchPacket::searchText,

            SearchPacket::new
    );
    
    public static void handle(SearchPacket packet, IPayloadContext pContext) {
        pContext.enqueueWork(() -> {
            Player player = pContext.player();

            AbstractSearchableBlockEntity blockEntity = (AbstractSearchableBlockEntity) player.level().getBlockEntity(packet.blockPos);

            if (blockEntity != null) {
                blockEntity.setSearchText(packet.searchText);
                blockEntity.setChanged();
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
