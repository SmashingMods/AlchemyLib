package com.smashingmods.alchemylib.api.network;

import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingMenu;
import com.smashingmods.alchemylib.common.network.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * AbstractPacketHandler is meant to be extended by other mods. It provides
 * some basic helper methods so that you don't need to build your own from
 * scratch for each mod.
 *
 * <p>Extend AlchemyPacket to easily create your own. Use the builtin packets
 * as an example for how they should work.</p>
 *
 * @see AlchemyPacket
 * @see BlockEntityPacket
 * @see SearchPacket
 * @see ToggleLockButtonPacket
 * @see TogglePauseButtonPacket
 */
@SuppressWarnings({"unused", "SameParameterValue"})
public abstract class AbstractPacketHandler {

    /**
     * The registrar used to register packets. It is created from the {@link RegisterPayloadHandlersEvent}
     * passed to {@link #register(RegisterPayloadHandlersEvent)} and is only valid for the duration of that
     * event, which is why packets must be registered there.
     */
    private PayloadRegistrar registrar;

    /**
     * This method should be used to register all of your packets. Add it as a listener for
     * {@link RegisterPayloadHandlersEvent} on your mod event bus so it runs while the network registry is
     * being set up.
     *
     * <p>Call {@link #registrar(RegisterPayloadHandlersEvent, String)} first to obtain a registrar for your
     * namespace, then register each packet with {@link #registerClientBound} or {@link #registerServerBound}.</p>
     *
     * @param pEvent {@link RegisterPayloadHandlersEvent}
     *
     * @see PacketHandler#register(RegisterPayloadHandlersEvent)
     */
    public abstract void register(RegisterPayloadHandlersEvent pEvent);

    /**
     * Obtains the {@link PayloadRegistrar} for your namespace from the event and stores it for the
     * {@link #registerClientBound} and {@link #registerServerBound} helpers. Call this at the start of your
     * {@link #register(RegisterPayloadHandlersEvent)} implementation.
     *
     * @param pEvent {@link RegisterPayloadHandlersEvent}
     * @param pNamespace The namespace (mod id) the packets belong to.
     *
     * @see PacketHandler#register(RegisterPayloadHandlersEvent)
     */
    protected void registrar(RegisterPayloadHandlersEvent pEvent, String pNamespace) {
        this.registrar = pEvent.registrar(pNamespace);
    }

    /**
     * Registers a packet that is sent from the server to the client. The packet's
     * {@link AlchemyPacket#handle(IPayloadContext) handle} method runs on the client side.
     *
     * @param pType The packet's {@link CustomPacketPayload.Type type}. Must match the
     *              {@link AlchemyPacket#type() type} the packet returns.
     * @param pCodec The {@link StreamCodec} that serializes the packet to and from the buffer.
     * @param <MSG> AlchemyPacket
     *
     * @see PacketHandler#register(RegisterPayloadHandlersEvent)
     * @see BlockEntityPacket#STREAM_CODEC
     */
    protected <MSG extends AlchemyPacket> void registerClientBound(CustomPacketPayload.Type<MSG> pType, StreamCodec<RegistryFriendlyByteBuf, MSG> pCodec) {
        registrar.playToClient(pType, pCodec, handle());
    }

    /**
     * Registers a packet that is sent from the client to the server. The packet's
     * {@link AlchemyPacket#handle(IPayloadContext) handle} method runs on the server side, where
     * {@link IPayloadContext#player()} resolves to the sending player.
     *
     * @param pType The packet's {@link CustomPacketPayload.Type type}. Must match the
     *              {@link AlchemyPacket#type() type} the packet returns.
     * @param pCodec The {@link StreamCodec} that serializes the packet to and from the buffer.
     * @param <MSG> AlchemyPacket
     *
     * @see PacketHandler#register(RegisterPayloadHandlersEvent)
     */
    protected <MSG extends AlchemyPacket> void registerServerBound(CustomPacketPayload.Type<MSG> pType, StreamCodec<RegistryFriendlyByteBuf, MSG> pCodec) {
        registrar.playToServer(pType, pCodec, handle());
    }

    /**
     * Adapts an {@link AlchemyPacket} to the {@link IPayloadHandler} expected by the payload registrar.
     * The packet is decoded on the network thread, so its {@link AlchemyPacket#handle(IPayloadContext) handle}
     * method is enqueued onto the main thread of the receiving side before it runs.
     *
     * @param <MSG> AlchemyPacket
     */
    private static <MSG extends AlchemyPacket> IPayloadHandler<MSG> handle() {
        return (pMessage, pContext) -> pContext.enqueueWork(() -> pMessage.handle(pContext));
    }

    /**
     * Sends the packet passed as a parameter to the server.
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
     * Sends a packet to the specific player specified in parameters.
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
     * Sends the packet passed as a parameter to all players connected to the server. Note: this will work
     * if you are in a single player instance, LAN, or a dedicated server.
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
        double posX = pBlockPos.getX();
        double posY = pBlockPos.getY();
        double posZ = pBlockPos.getZ();
        PacketDistributor.sendToPlayersNear((ServerLevel) pLevel, null, posX, posY, posZ, pRadius, pMessage);
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
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) pLevel, new ChunkPos(pBlockPos), pMessage);
    }
}
