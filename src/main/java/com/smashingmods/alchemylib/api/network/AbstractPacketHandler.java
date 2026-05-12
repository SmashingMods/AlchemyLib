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

    /**
     * Sends the packet passed as a parameter to the server via {@link PacketDistributor}.
     *
     * @param pMessage Your packet to send to the server.
     * @param <MSG> extends AlchemyPacket
     *
     * @see AlchemyPacket
     */
    public <MSG extends AlchemyPacket> void sendToServer(MSG pMessage) {
        PacketDistributor.sendToServer(pMessage);
    }

    /**
     * Sends a packet to the specific player specified in parameters via {@link PacketDistributor}.
     *
     * @param pMessage Your packet to send to the player.
     * @param pPlayer And instance of ServerPlayer.
     * @param <MSG> AlchemyPacket
     *
     * @see AlchemyPacket
     */
    public <MSG extends AlchemyPacket> void sendToPlayer(MSG pMessage, ServerPlayer pPlayer) {
        PacketDistributor.sendToPlayer(pPlayer, pMessage);
    }

    /**
     * Sends the packet passed as a parameter to all players connected to the server via
     * {@link PacketDistributor}. Note: this will work if you are in a single player instance,
     * LAN, or a dedicated server.
     *
     * @param pMessage Your packet to send to all players.
     * @param <MSG> AlchemyPacket
     */
    public <MSG extends AlchemyPacket> void sendToAll(MSG pMessage) {
        PacketDistributor.sendToAllPlayers(pMessage);
    }

    /**
     * Sends the packet passed as a parameter to all players within a radius of the passed {@link BlockPos}
     * in the {@link Level} parameter.
     *
     * @param pMessage Your packet to send.
     * @param pLevel An instance of the Level (overworld, nether, end, etc) used to determine the context
     *               of the BlockPos parameter.
     * @param pBlockPos BlockPos that is the center location for where to send the packet.
     * @param pRadius Distance in blocks from the center BlockPos, the packet is sent to everyone in this radius.
     * @param <MSG> AlchemyPacket
     *
     */
    public <MSG extends AlchemyPacket> void sendToNear(MSG pMessage, Level pLevel, BlockPos pBlockPos, double pRadius) {
        if (pLevel instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersNear(serverLevel, null,
                    pBlockPos.getX(), pBlockPos.getY(), pBlockPos.getZ(),
                    pRadius, pMessage);
        }
    }

    /**
     * Sends the packet to all players that are tracking a specific chunk based on the passed {@link Level} and {@link BlockPos}.
     * All players that are tracking the chunk of the BlockPos will receive the packet.
     *
     * @param pMessage Your packet to send.
     * @param pLevel An instance of the Level (overworld, nether, end, etc) used to determine the context
     *               of the BlockPos parameter.
     * @param pBlockPos BlockPos used to find the chunk being tracked.
     * @param <MSG> AlchemyPacket
     *
     * @see Level
     * @see BlockPos
     */
    public <MSG extends AlchemyPacket> void sendToTrackingChunk(MSG pMessage, Level pLevel, BlockPos pBlockPos) {
        if (pLevel instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new ChunkPos(pBlockPos), pMessage);
        }
    }
}
