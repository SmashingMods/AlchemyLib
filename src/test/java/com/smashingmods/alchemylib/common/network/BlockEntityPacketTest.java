package com.smashingmods.alchemylib.common.network;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Round-trip test for {@link BlockEntityPacket}. Its two fields are private with no getters, so the round-trip
 * is asserted two ways: by re-reading the encoded buffer in the same field order (direct field values), and by
 * re-encoding the decoded packet and comparing the bytes (faithful end-to-end equivalence). This guards the
 * encode/decode field order; it does not exercise {@link BlockEntityPacket#handle} wiring.
 */
class BlockEntityPacketTest {

    @Test
    void blockEntityPacket_roundTrip() {
        BlockPos pos = new BlockPos(12, -34, 56);
        CompoundTag tag = new CompoundTag();
        tag.putInt("heat", 42);
        tag.putString("recipe", "alchemylib:test");

        BlockEntityPacket original = new BlockEntityPacket(pos, tag);

        // Encode, then decode via the buffer constructor, then re-encode the decoded copy.
        FriendlyByteBuf encoded = new FriendlyByteBuf(Unpooled.buffer());
        original.write(encoded);
        byte[] encodedBytes = readableBytes(encoded);

        // The encode order is blockPos, tag -- re-read it to assert the field values survive the trip.
        FriendlyByteBuf forFields = new FriendlyByteBuf(Unpooled.wrappedBuffer(encodedBytes));
        assertEquals(pos, forFields.readBlockPos());
        assertEquals(tag, forFields.readNbt());

        BlockEntityPacket decoded = new BlockEntityPacket(new FriendlyByteBuf(Unpooled.wrappedBuffer(encodedBytes)));
        FriendlyByteBuf reEncoded = new FriendlyByteBuf(Unpooled.buffer());
        decoded.write(reEncoded);

        assertArrayEquals(encodedBytes, readableBytes(reEncoded));
    }

    private static byte[] readableBytes(FriendlyByteBuf buffer) {
        byte[] bytes = new byte[buffer.readableBytes()];
        buffer.getBytes(buffer.readerIndex(), bytes);
        return bytes;
    }
}
