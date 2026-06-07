package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class ToggleLockButtonPacket implements AlchemyPacket {

    public static final ResourceLocation ID = new ResourceLocation(AlchemyLib.MODID, "toggle_lock_button");

    private final BlockPos blockPos;
    private final boolean locked;

    public ToggleLockButtonPacket(BlockPos pBlockPos, boolean pLock) {
        this.blockPos = pBlockPos;
        this.locked = pLock;
    }

    public ToggleLockButtonPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.locked = pBuffer.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(locked);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handle(PlayPayloadContext pContext) {
        pContext.player().ifPresent(player -> {
            if (player.level().getBlockEntity(blockPos) instanceof AbstractProcessingBlockEntity blockEntity) {
                blockEntity.setRecipeLocked(locked);
                blockEntity.setChanged();
            }
        });
    }
}
