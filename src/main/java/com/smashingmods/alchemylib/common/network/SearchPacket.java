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
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SearchPacket implements AlchemyPacket {

    public static final Type<SearchPacket> TYPE = new Type<>(new ResourceLocation(AlchemyLib.MODID, "search"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SearchPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, packet -> packet.blockPos,
            ByteBufCodecs.STRING_UTF8, packet -> packet.searchText,
            SearchPacket::new
    );

    private final BlockPos blockPos;
    private final String searchText;

    public SearchPacket(BlockPos pBlockPos, String pSearchText) {
        this.blockPos = pBlockPos;
        this.searchText = pSearchText;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(IPayloadContext pContext) {
        if (pContext.player().level().getBlockEntity(blockPos) instanceof AbstractSearchableBlockEntity blockEntity) {
            blockEntity.setSearchText(searchText);
            blockEntity.setChanged();
        }
    }
}
