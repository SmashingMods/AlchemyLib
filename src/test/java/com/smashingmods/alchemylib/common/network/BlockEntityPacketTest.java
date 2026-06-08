package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.testsupport.BootstrappedTest;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Round-trip test for {@link BlockEntityPacket}. Its two fields are private with no getters, so the round-trip
 * is asserted two ways: by re-reading the encoded buffer in the same field order (direct field values), and by
 * re-encoding the decoded packet and comparing the bytes (faithful end-to-end equivalence). This guards the
 * encode/decode field order; it does not exercise {@link BlockEntityPacket#handle} wiring.
 *
 * <p>Serialization runs through {@link BlockEntityPacket#STREAM_CODEC} over a
 * {@link RegistryFriendlyByteBuf}, the registry-aware buffer that buffer is bound to on the network.</p>
 */
class BlockEntityPacketTest extends BootstrappedTest {

    @Test
    void blockEntityPacket_roundTrip() {
        BlockPos pos = new BlockPos(12, -34, 56);
        CompoundTag tag = new CompoundTag();
        tag.putInt("heat", 42);
        tag.putString("recipe", "alchemylib:test");

        BlockEntityPacket original = new BlockEntityPacket(pos, tag);

        // Encode via the stream codec, then decode it back, then re-encode the decoded copy.
        RegistryFriendlyByteBuf encoded = registryBuffer();
        BlockEntityPacket.STREAM_CODEC.encode(encoded, original);
        byte[] encodedBytes = readableBytes(encoded);

        // The encode order is blockPos, tag -- re-read it to assert the field values survive the trip.
        RegistryFriendlyByteBuf forFields = registryBuffer();
        forFields.writeBytes(encodedBytes);
        assertEquals(pos, forFields.readBlockPos());
        assertEquals(tag, forFields.readNbt());

        RegistryFriendlyByteBuf forDecode = registryBuffer();
        forDecode.writeBytes(encodedBytes);
        BlockEntityPacket decoded = BlockEntityPacket.STREAM_CODEC.decode(forDecode);

        RegistryFriendlyByteBuf reEncoded = registryBuffer();
        BlockEntityPacket.STREAM_CODEC.encode(reEncoded, decoded);

        assertArrayEquals(encodedBytes, readableBytes(reEncoded));
    }

    private static byte[] readableBytes(RegistryFriendlyByteBuf buffer) {
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), bytes);
        return bytes;
    }
}
