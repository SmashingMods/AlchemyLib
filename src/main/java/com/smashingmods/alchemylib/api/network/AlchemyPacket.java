package com.smashingmods.alchemylib.api.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Implement AlchemyPacket to create your own payloads. Each implementation must:
 * <ul>
 *   <li>declare a {@code public static final Type<P> TYPE = new Type<>(ResourceLocation)};</li>
 *   <li>declare a {@code public static final StreamCodec<RegistryFriendlyByteBuf, P> STREAM_CODEC};</li>
 *   <li>return TYPE from {@link #type()};</li>
 *   <li>handle the received payload in {@link #handle(IPayloadContext)}.</li>
 * </ul>
 *
 * Register the packet via {@link AbstractPacketHandler#register(net.neoforged.neoforge.network.registration.PayloadRegistrar)}.
 */
public interface AlchemyPacket extends CustomPacketPayload {

    /**
     * Handle the received payload. Use {@link IPayloadContext#enqueueWork(Runnable)} for any
     * world-mutation that must run on the main thread.
     */
    void handle(IPayloadContext pContext);
}
