package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.AlchemyLib;
import com.smashingmods.alchemylib.api.blockentity.processing.AbstractProcessingBlockEntity;
import com.smashingmods.alchemylib.api.network.AlchemyPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public class TogglePauseButtonPacket implements AlchemyPacket {

    public static final ResourceLocation ID = new ResourceLocation(AlchemyLib.MODID, "toggle_pause_button");

    private final BlockPos blockPos;
    private final boolean paused;

    public TogglePauseButtonPacket(BlockPos pBlockPos, boolean pPause) {
        this.blockPos = pBlockPos;
        this.paused = pPause;
    }

    public TogglePauseButtonPacket(FriendlyByteBuf pBuffer) {
        this.blockPos = pBuffer.readBlockPos();
        this.paused = pBuffer.readBoolean();
    }

    @Override
    public void write(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(blockPos);
        pBuffer.writeBoolean(paused);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void handle(PlayPayloadContext pContext) {
        pContext.player().ifPresent(player -> {
            if (player.level().getBlockEntity(blockPos) instanceof AbstractProcessingBlockEntity blockEntity) {
                blockEntity.setPaused(paused);
                blockEntity.setChanged();
            }
        });
    }
}
