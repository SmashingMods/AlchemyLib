package com.smashingmods.alchemylib.common.network;

import com.smashingmods.alchemylib.testsupport.BootstrappedTest;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Round-trip contracts for the four packets' {@link StreamCodec}s. The 1.20.6 payload API moved each packet's
 * serialization out of an imperative {@code write} method and into a {@code STREAM_CODEC} field, so an
 * encode/decode round trip per packet is a contract surface that did not exist before.
 *
 * <p>Every packet's fields are private with no getters, so the round trip is asserted by re-encoding the
 * decoded packet and comparing the bytes against the original encoding. To keep that non-vacuous -- a codec
 * that dropped a field would still round-trip the bytes it does write -- each packet is also paired with a
 * variant that differs in exactly one field, and the two must encode to different byte arrays. That proves the
 * codec actually serializes each field: were a composite component paired with the wrong field accessor, the
 * differing-field variant would collide with the original and the distinctness assertion would fail.</p>
 */
class PacketStreamCodecRoundTripTest extends BootstrappedTest {

    private static final BlockPos POS = new BlockPos(12, -34, 56);
    private static final BlockPos OTHER_POS = new BlockPos(-7, 8, 9);

    @Test
    void blockEntityPacket_roundTripsAndSerializesEachField() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("heat", 42);
        tag.putString("recipe", "alchemylib:test");
        CompoundTag otherTag = new CompoundTag();
        otherTag.putInt("heat", 7);

        assertRoundTrips(BlockEntityPacket.STREAM_CODEC, new BlockEntityPacket(POS, tag));
        // pos field differs
        assertEncodingsDiffer(BlockEntityPacket.STREAM_CODEC,
                new BlockEntityPacket(POS, tag), new BlockEntityPacket(OTHER_POS, tag));
        // tag field differs
        assertEncodingsDiffer(BlockEntityPacket.STREAM_CODEC,
                new BlockEntityPacket(POS, tag), new BlockEntityPacket(POS, otherTag));
    }

    @Test
    void searchPacket_roundTripsAndSerializesEachField() {
        assertRoundTrips(SearchPacket.STREAM_CODEC, new SearchPacket(POS, "iron"));
        // pos field differs
        assertEncodingsDiffer(SearchPacket.STREAM_CODEC,
                new SearchPacket(POS, "iron"), new SearchPacket(OTHER_POS, "iron"));
        // searchText field differs
        assertEncodingsDiffer(SearchPacket.STREAM_CODEC,
                new SearchPacket(POS, "iron"), new SearchPacket(POS, "gold"));
    }

    @Test
    void toggleLockButtonPacket_roundTripsAndSerializesEachField() {
        assertRoundTrips(ToggleLockButtonPacket.STREAM_CODEC, new ToggleLockButtonPacket(POS, true));
        // pos field differs
        assertEncodingsDiffer(ToggleLockButtonPacket.STREAM_CODEC,
                new ToggleLockButtonPacket(POS, true), new ToggleLockButtonPacket(OTHER_POS, true));
        // locked field differs
        assertEncodingsDiffer(ToggleLockButtonPacket.STREAM_CODEC,
                new ToggleLockButtonPacket(POS, true), new ToggleLockButtonPacket(POS, false));
    }

    @Test
    void togglePauseButtonPacket_roundTripsAndSerializesEachField() {
        assertRoundTrips(TogglePauseButtonPacket.STREAM_CODEC, new TogglePauseButtonPacket(POS, true));
        // pos field differs
        assertEncodingsDiffer(TogglePauseButtonPacket.STREAM_CODEC,
                new TogglePauseButtonPacket(POS, true), new TogglePauseButtonPacket(OTHER_POS, true));
        // paused field differs
        assertEncodingsDiffer(TogglePauseButtonPacket.STREAM_CODEC,
                new TogglePauseButtonPacket(POS, true), new TogglePauseButtonPacket(POS, false));
    }

    private static <T> void assertRoundTrips(StreamCodec<RegistryFriendlyByteBuf, T> codec, T packet) {
        byte[] encoded = encode(codec, packet);

        RegistryFriendlyByteBuf forDecode = registryBuffer();
        forDecode.writeBytes(encoded);
        T decoded = codec.decode(forDecode);

        assertArrayEquals(encoded, encode(codec, decoded));
    }

    private static <T> void assertEncodingsDiffer(StreamCodec<RegistryFriendlyByteBuf, T> codec, T first, T second) {
        assertFalse(java.util.Arrays.equals(encode(codec, first), encode(codec, second)),
                "packets that differ in one field must not encode identically");
    }

    private static <T> byte[] encode(StreamCodec<RegistryFriendlyByteBuf, T> codec, T packet) {
        RegistryFriendlyByteBuf buffer = registryBuffer();
        codec.encode(buffer, packet);
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), bytes);
        return bytes;
    }
}
