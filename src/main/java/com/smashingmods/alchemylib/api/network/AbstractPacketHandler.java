package com.smashingmods.alchemylib.api.network;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * AbstractPacketHandler is meant to be extended by mods. It exposes typed send-helpers
 * over NeoForge's {@link PacketDistributor} and a single {@link #register(PayloadRegistrar)}
 * hook called from a {@code RegisterPayloadHandlersEvent} listener.
 */
@SuppressWarnings({"unused", "SameParameterValue"})
public abstract class AbstractPacketHandler {

    /**
     * Register all of your mod's packets here. Called once from a
     * {@code RegisterPayloadHandlersEvent} listener with a registrar pre-scoped to your modid.
     */
    public abstract void register(PayloadRegistrar registrar);

    public <MSG extends AlchemyPacket> void sendToServer(MSG pMessage) {
        PacketDistributor.sendToServer(pMessage);
    }

    public <MSG extends AlchemyPacket> void sendToPlayer(MSG pMessage, ServerPlayer pPlayer) {
        PacketDistributor.sendToPlayer(pPlayer, pMessage);
    }

    public <MSG extends AlchemyPacket> void sendToAll(MSG pMessage) {
        PacketDistributor.sendToAllPlayers(pMessage);
    }

    public <MSG extends AlchemyPacket> void sendToNear(MSG pMessage, Level pLevel, BlockPos pBlockPos, double pRadius) {
        if (pLevel instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersNear(serverLevel, null,
                    pBlockPos.getX(), pBlockPos.getY(), pBlockPos.getZ(),
                    pRadius, pMessage);
        }
    }

    public <MSG extends AlchemyPacket> void sendToTrackingChunk(MSG pMessage, Level pLevel, BlockPos pBlockPos) {
        if (pLevel instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(pBlockPos), pMessage);
        }
    }
}
