package com.smashingmods.alchemylib.api.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.Function;

@SuppressWarnings("SameParameterValue")
public abstract class AbstractPacketHandler {

    private static int PACKET_ID;

    public AbstractPacketHandler() {

    }

    protected static SimpleChannel createChannel(ResourceLocation pChannelName, String pProtocolVersion) {
        return NetworkRegistry.ChannelBuilder.named(pChannelName)
                .clientAcceptedVersions(pProtocolVersion::equals)
                .serverAcceptedVersions(pProtocolVersion::equals)
                .networkProtocolVersion(() -> pProtocolVersion)
                .simpleChannel();
    }

    public abstract void register();

    protected abstract SimpleChannel getChannel();

    protected <MSG extends AlchemyPacket> void registerClientToServer(Class<MSG> pMessageType, Function<FriendlyByteBuf, MSG> pDecoder) {
        registerMessage(pMessageType, pDecoder, NetworkDirection.PLAY_TO_SERVER);
    }

    protected <MSG extends AlchemyPacket> void registerServerToClient(Class<MSG> pMessageType, Function<FriendlyByteBuf, MSG> pDecoder) {
        registerMessage(pMessageType, pDecoder, NetworkDirection.PLAY_TO_CLIENT);
    }

    protected <MSG extends AlchemyPacket> void registerMessage(Class<MSG> pMessageType, Function<FriendlyByteBuf, MSG> pDecoder, NetworkDirection pDirection) {
        getChannel().registerMessage(PACKET_ID++, pMessageType, AlchemyPacket::encode, pDecoder, AlchemyPacket::handle, Optional.of(pDirection));
    }

    public <MSG> void sendToServer(MSG pMessage) {
        getChannel().sendToServer(pMessage);
    }

    @SuppressWarnings("unused")
    public <MSG> void sendToPlayer(MSG pMessage, ServerPlayer pPlayer) {
        getChannel().send(PacketDistributor.PLAYER.with(() -> pPlayer), pMessage);
    }

    @SuppressWarnings("unused")
    public <MSG> void sendToAll(MSG pMessage) {
         getChannel().send(PacketDistributor.ALL.noArg(), pMessage);
    }

    @SuppressWarnings("unused")
    public <MSG> void sendToNear(MSG pMessage, Level pLevel, BlockPos pBlockPos, double pRadius) {
        ResourceKey<Level> dimension = pLevel.dimension();
        double posX = pBlockPos.getX();
        double posY = pBlockPos.getY();
        double posZ = pBlockPos.getZ();
        getChannel().send(PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(posX, posY, posZ, pRadius, dimension)), pMessage);
    }

    public <MSG> void sendToTrackingChunk(MSG pMessage, Level pLevel, BlockPos pBlockPos) {
        LevelChunk levelChunk = pLevel.getChunkAt(pBlockPos);
        getChannel().send(PacketDistributor.TRACKING_CHUNK.with(() -> levelChunk), pMessage);
    }
}
