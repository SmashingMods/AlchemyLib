package com.smashingmods.alchemylib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Smoke test: proves the {@code test} source set runs JUnit Platform at all.
 * No Minecraft classpath required.
 */
class HarnessSmokeTest {

    @Test
    void junitPlatformRunsTheTestSourceSet() {
        assertTrue(true);
    }
}
