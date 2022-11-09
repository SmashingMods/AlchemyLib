package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;

public class BlockEntityPacket implements AlchemyPacket {

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

    public void encode(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeNbt(tag);
    }

    public void handle(NetworkEvent.Context pContext) {
        Level level = Minecraft.getInstance().level;
        BlockEntity blockEntity = Objects.requireNonNull(level).getBlockEntity(blockPos);
        Objects.requireNonNull(blockEntity).load(tag);
    }
}
