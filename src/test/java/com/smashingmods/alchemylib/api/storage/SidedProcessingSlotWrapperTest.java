package com.smashingmods.alchemylib.api.storage;

import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tier-0 tests for the side-mode short pack/unpack on {@link SidedProcessingSlotWrapper}.
 *
 * <p>The wrapper is built with {@code new SidedProcessingSlotWrapper(null, null)}: pack/unpack and
 * {@code get}/{@code setSideMode} only read and write the {@code sideModes} array, so the (null)
 * input/output handlers and the inner {@code SidedItemHandlerView} are never touched.</p>
 *
 * <p>No {@link net.minecraft.server.Bootstrap} is required -- {@link Direction} is a plain enum and
 * the wrapper's {@code ItemStack}/{@code IItemHandler} references are only field and inner-class
 * types that pack/unpack does not force to initialise.</p>
 */
class SidedProcessingSlotWrapperTest {

    @Test
    void sideModeRoundTrip_allSidesIncludingNull_isIdentity() {
        SidedProcessingSlotWrapper wrapper = new SidedProcessingSlotWrapper(null, null);

        // A distinct mode per side, covering all six Directions plus the null (unspecified) side.
        wrapper.setSideMode(Direction.DOWN, SideMode.PUSH);
        wrapper.setSideMode(Direction.UP, SideMode.PULL);
        wrapper.setSideMode(Direction.NORTH, SideMode.DISABLED);
        wrapper.setSideMode(Direction.SOUTH, SideMode.ENABLED);
        wrapper.setSideMode(Direction.WEST, SideMode.PULL);
        wrapper.setSideMode(Direction.EAST, SideMode.PUSH);
        wrapper.setSideMode(null, SideMode.DISABLED);

        wrapper.setSideModesFromShort(wrapper.sideModesToShort());

        assertEquals(SideMode.PUSH, wrapper.getSideMode(Direction.DOWN));
        assertEquals(SideMode.PULL, wrapper.getSideMode(Direction.UP));
        assertEquals(SideMode.DISABLED, wrapper.getSideMode(Direction.NORTH));
        assertEquals(SideMode.ENABLED, wrapper.getSideMode(Direction.SOUTH));
        assertEquals(SideMode.PULL, wrapper.getSideMode(Direction.WEST));
        assertEquals(SideMode.PUSH, wrapper.getSideMode(Direction.EAST));
        assertEquals(SideMode.DISABLED, wrapper.getSideMode(null));
    }

    @Test
    void sideModeRoundTrip_allEnabled_isIdentity() {
        // The constructor seeds every side to ENABLED; the default state must also round-trip.
        SidedProcessingSlotWrapper wrapper = new SidedProcessingSlotWrapper(null, null);

        wrapper.setSideModesFromShort(wrapper.sideModesToShort());

        for (Direction direction : Direction.values()) {
            assertEquals(SideMode.ENABLED, wrapper.getSideMode(direction));
        }
        assertEquals(SideMode.ENABLED, wrapper.getSideMode(null));
    }

    @Test
    void sideModeResidualThrows_bitBeyondSevenSideRegion() {
        SidedProcessingSlotWrapper wrapper = new SidedProcessingSlotWrapper(null, null);

        // Seven sides occupy bits 0..13 (2 bits each); bit 14 is the first that cannot be consumed,
        // so it must be reported as an unclean residual.
        assertThrows(IllegalArgumentException.class, () -> wrapper.setSideModesFromShort(1 << 14));
    }

    @Test
    void legacySidesConfig_unpacksToPullUpWestPushDownEastDisabledRest() {
        SidedProcessingSlotWrapper wrapper = new SidedProcessingSlotWrapper(null, null);

        wrapper.setSideModesFromShort(SidedProcessingSlotWrapper.LEGACY_SIDES_CONFIGURATION);

        assertEquals(SideMode.PULL, wrapper.getSideMode(Direction.UP));
        assertEquals(SideMode.PULL, wrapper.getSideMode(Direction.WEST));
        assertEquals(SideMode.PUSH, wrapper.getSideMode(Direction.DOWN));
        assertEquals(SideMode.PUSH, wrapper.getSideMode(Direction.EAST));
        assertEquals(SideMode.DISABLED, wrapper.getSideMode(Direction.NORTH));
        assertEquals(SideMode.DISABLED, wrapper.getSideMode(Direction.SOUTH));
        assertEquals(SideMode.DISABLED, wrapper.getSideMode(null));
    }
}
