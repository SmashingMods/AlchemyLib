package com.smashingmods.alchemylib.api.network;

import com.smashingmods.alchemylib.api.blockentity.container.AbstractProcessingMenu;
import com.smashingmods.alchemylib.common.network.*;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPlayPayloadHandler;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

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
     * The registrar used to register packets. It is created from the {@link RegisterPayloadHandlerEvent}
     * passed to {@link #register(RegisterPayloadHandlerEvent)} and is only valid for the duration of that
     * event, which is why packets must be registered there.
     */
    private IPayloadRegistrar registrar;

    /**
     * This method should be used to register all of your packets. Add it as a listener for
     * {@link RegisterPayloadHandlerEvent} on your mod event bus so it runs while the network registry is
     * being set up.
     *
     * <p>Call {@link #registrar(RegisterPayloadHandlerEvent, String)} first to obtain a registrar for your
     * namespace, then register each packet with {@link #registerClientBound} or {@link #registerServerBound}.</p>
     *
     * @param pEvent {@link RegisterPayloadHandlerEvent}
     *
     * @see PacketHandler#register(RegisterPayloadHandlerEvent)
     */
    public abstract void register(RegisterPayloadHandlerEvent pEvent);

    /**
     * Obtains the {@link IPayloadRegistrar} for your namespace from the event and stores it for the
     * {@link #registerClientBound} and {@link #registerServerBound} helpers. Call this at the start of your
     * {@link #register(RegisterPayloadHandlerEvent)} implementation.
     *
     * @param pEvent {@link RegisterPayloadHandlerEvent}
     * @param pNamespace The namespace (mod id) the packets belong to.
     *
     * @see PacketHandler#register(RegisterPayloadHandlerEvent)
     */
    protected void registrar(RegisterPayloadHandlerEvent pEvent, String pNamespace) {
        this.registrar = pEvent.registrar(pNamespace);
    }

    /**
     * Registers a packet that is sent from the server to the client. The packet's
     * {@link AlchemyPacket#handle(net.neoforged.neoforge.network.handling.PlayPayloadContext) handle} method
     * runs on the client side.
     *
     * @param pId The packet's id. Must match the {@link AlchemyPacket#id()} the packet returns.
     * @param pDecoder A function that takes a {@link FriendlyByteBuf} and returns a packet.
     *                 Typically, you want this to be a constructor on your packet,
     *                 but it can also be a static method that returns a new object.
     * @param <MSG> AlchemyPacket
     *
     * @see PacketHandler#register(RegisterPayloadHandlerEvent)
     * @see BlockEntityPacket#BlockEntityPacket(FriendlyByteBuf)  BlockEntityPacket
     */
    protected <MSG extends AlchemyPacket> void registerClientBound(ResourceLocation pId, FriendlyByteBuf.Reader<MSG> pDecoder) {
        registrar.play(pId, pDecoder, handler -> handler.client(AbstractPacketHandler::handle));
    }

    /**
     * Registers a packet that is sent from the client to the server. The packet's
     * {@link AlchemyPacket#handle(net.neoforged.neoforge.network.handling.PlayPayloadContext) handle} method
     * runs on the server side, where {@link net.neoforged.neoforge.network.handling.IPayloadContext#player()}
     * resolves to the sending player.
     *
     * @param pId The packet's id. Must match the {@link AlchemyPacket#id()} the packet returns.
     * @param pDecoder A function that takes a {@link FriendlyByteBuf} and returns a packet.
     * @param <MSG> AlchemyPacket
     *
     * @see PacketHandler#register(RegisterPayloadHandlerEvent)
     */
    protected <MSG extends AlchemyPacket> void registerServerBound(ResourceLocation pId, FriendlyByteBuf.Reader<MSG> pDecoder) {
        registrar.play(pId, pDecoder, handler -> handler.server(AbstractPacketHandler::handle));
    }

    /**
     * Adapts an {@link AlchemyPacket} to the {@link IPlayPayloadHandler} expected by the payload registrar.
     * The packet is decoded on the network thread, so its {@link AlchemyPacket#handle(PlayPayloadContext) handle}
     * method is enqueued onto the main thread of the receiving side before it runs.
     *
     * @param pMessage The implementing packet.
     * @param pContext {@link PlayPayloadContext} supplied by the network layer.
     * @param <MSG> AlchemyPacket
     */
    private static <MSG extends AlchemyPacket> void handle(final MSG pMessage, PlayPayloadContext pContext) {
        pContext.workHandler().execute(() -> pMessage.handle(pContext));
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
        PacketDistributor.SERVER.noArg().send(pMessage);
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
        PacketDistributor.PLAYER.with(pPlayer).send(pMessage);
    }

    /**
     * Sends the packet passed as a parameter to all players connected to the server. Note: this will work
     * if you are in a single player instance, LAN, or a dedicated server.
     *
     * @param pMessage Your packet to send to all players.
     * @param <MSG> AlchemyPacket
     */
    public <MSG extends AlchemyPacket> void sendToAll(MSG pMessage) {
         PacketDistributor.ALL.noArg().send(pMessage);
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
        ResourceKey<Level> dimension = pLevel.dimension();
        double posX = pBlockPos.getX();
        double posY = pBlockPos.getY();
        double posZ = pBlockPos.getZ();
        PacketDistributor.NEAR.with(new PacketDistributor.TargetPoint(posX, posY, posZ, pRadius, dimension)).send(pMessage);
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
        LevelChunk levelChunk = pLevel.getChunkAt(pBlockPos);
        PacketDistributor.TRACKING_CHUNK.with(levelChunk).send(pMessage);
    }
}
