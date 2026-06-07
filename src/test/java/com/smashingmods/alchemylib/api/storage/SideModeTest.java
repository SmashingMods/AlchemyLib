package com.smashingmods.alchemylib.api.storage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link SideMode}: pure enum flag/ordinal math, no Minecraft classpath
 * and no {@link net.minecraft.server.Bootstrap} required.
 *
 * <p>Pins the pull/push flag pairs of each constant and the {@link SideMode#getFromOrdinal(int)}
 * mapping, both of which the side-mode short pack/unpack in {@link SidedProcessingSlotWrapper}
 * relies on staying stable.</p>
 */
class SideModeTest {

    @Test
    void sideModeEnum_disabled_isNeitherPullNorPush() {
        assertFalse(SideMode.DISABLED.isPullEnabled());
        assertFalse(SideMode.DISABLED.isPushEnabled());
    }

    @Test
    void sideModeEnum_pull_isPullOnly() {
        assertTrue(SideMode.PULL.isPullEnabled());
        assertFalse(SideMode.PULL.isPushEnabled());
    }

    @Test
    void sideModeEnum_push_isPushOnly() {
        assertFalse(SideMode.PUSH.isPullEnabled());
        assertTrue(SideMode.PUSH.isPushEnabled());
    }

    @Test
    void sideModeEnum_enabled_isBothPullAndPush() {
        assertTrue(SideMode.ENABLED.isPullEnabled());
        assertTrue(SideMode.ENABLED.isPushEnabled());
    }

    @Test
    void sideModeEnum_getFromOrdinal_mapsToDeclarationOrder() {
        assertEquals(SideMode.DISABLED, SideMode.getFromOrdinal(0));
        assertEquals(SideMode.PULL, SideMode.getFromOrdinal(1));
        assertEquals(SideMode.PUSH, SideMode.getFromOrdinal(2));
        assertEquals(SideMode.ENABLED, SideMode.getFromOrdinal(3));
    }
}
