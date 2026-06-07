package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class BlockEntityPacket implements AlchemyPacket {

    public static final ResourceLocation ID = new ResourceLocation(AlchemyLib.MODID, "block_entity");

    private final BlockPos blockPos;
    private final CompoundTag tag;

    public BlockEntityPacket(BlockPos pBlockPos, CompoundTag pTag) {
        this.blockPos = pBlockPos;
        this.tag = pTag;
    }

    public BlockEntityPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.tag = pBuffer.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeNbt(tag);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handle(PlayPayloadContext pContext) {
        pContext.player().ifPresent(player -> {
            if (player.level().getBlockEntity(blockPos) instanceof AbstractProcessingBlockEntity blockEntity) {
                blockEntity.load(tag);
            }
        });
    }
}
