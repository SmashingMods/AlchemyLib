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
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
     * All packets need to have a valid unique discriminator. AbstractPacketHandler
     * sets a private int PACKET_ID that implementing classes will need to increment.
     */
    private int PACKET_ID;

    /**
     * To send packets, you have to have a SimpleChannel registered in the NetworkRegistry.
     *
     * @param pChannelName ResourceLocation for your {@link SimpleChannel}.
     * @param pProtocolVersion String representation of your protocol version (for example "1.0.0").
     * @return a new SimpleChannel registered in the {@link NetworkRegistry}.
     *
     * @see NetworkRegistry#newSimpleChannel(ResourceLocation, Supplier, Predicate, Predicate)
     */
    protected static SimpleChannel createChannel(ResourceLocation pChannelName, String pProtocolVersion) {
        return NetworkRegistry.newSimpleChannel(pChannelName, () -> pProtocolVersion, pProtocolVersion::equals, pProtocolVersion::equals);
    }

    /**
     * This method should be used to register all of your packets. After registering, return
     * the instance of your PacketHandler so that it can be used to send packets.
     *
     * @return an implementation of AbstractPacketHandler.
     *
     * @see PacketHandler#register()
     */
    public abstract AbstractPacketHandler register();

    /**
     * Implement this getter to return your own instance of {@link SimpleChannel} on your
     * PacketHandler class. This is used to get the channel for sending packets
     * by other parts of your mod.
     *
     * @return {@link SimpleChannel}
     *
     * @see AbstractProcessingMenu#broadcastChanges()
     */
    protected abstract SimpleChannel getChannel();

    /**
     * All packets must be registered on your SimpleChannel. Call this method in your
     * implementation class's register method.
     *
     * @param pMessageType Class of your packet. (for example BlockEntityPacket.class)
     * @param pDecoder A function that takes a {@link FriendlyByteBuf} and returns a packet.
     *                 Typically, you want this to be a constructor on your packet,
     *                 but it can also be a static method that returns a new object.
     * @param <MSG> AlchemyPacket
     *
     * @see PacketHandler#register()
     * @see BlockEntityPacket#BlockEntityPacket(FriendlyByteBuf)  BlockEntityPacket
     */
    protected <MSG extends AlchemyPacket> void registerMessage(Class<MSG> pMessageType, Function<FriendlyByteBuf, MSG> pDecoder) {
        getChannel().registerMessage(PACKET_ID++, pMessageType, AlchemyPacket::encode, pDecoder, AlchemyPacket::handle);
    }

    /**
     * Sends the packet passed as a parameter to the server via your {@link SimpleChannel}.
     *
     * @param pMessage Your packet to send to the server.
     * @param <MSG> extends AlchemyPacket
     *
     * @see AlchemyPacket
     */
    public <MSG extends AlchemyPacket> void sendToServer(MSG pMessage) {
        getChannel().sendToServer(pMessage);
    }

    /**
     * Sends a packet to the specific player specified in parameters via your {@link SimpleChannel}.
     *
     * @param pMessage Your packet to send to the player.
     * @param pPlayer And instance of ServerPlayer.
     * @param <MSG> AlchemyPacket
     *
     * @see AlchemyPacket
     */
    public <MSG extends AlchemyPacket> void sendToPlayer(MSG pMessage, ServerPlayer pPlayer) {
        getChannel().send(PacketDistributor.PLAYER.with(() -> pPlayer), pMessage);
    }

    /**
     * Sends the packet passed as a parameter to all players connected to the server via
     * your {@link SimpleChannel}. Note: this will work if you are in a single player instance,
     * LAN, or a dedicated server.
     *
     * @param pMessage Your packet to send to all players.
     * @param <MSG> AlchemyPacket
     */
    public <MSG extends AlchemyPacket> void sendToAll(MSG pMessage) {
         getChannel().send(PacketDistributor.ALL.noArg(), pMessage);
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
        getChannel().send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(posX, posY, posZ, pRadius, dimension)), pMessage);
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
        getChannel().send(PacketDistributor.TRACKING_CHUNK.with(() -> levelChunk), pMessage);
    }
}
