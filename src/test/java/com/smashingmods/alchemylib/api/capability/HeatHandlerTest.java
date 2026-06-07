package com.smashingmods.alchemylib.api.capability;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tier-0 tests for {@link HeatHandler}'s {@code increment}/{@code decrement} clamping: pure int
 * arithmetic, no Minecraft classpath and no {@link net.minecraft.server.Bootstrap} required.
 *
 * <p>{@link HeatHandler#serializeNBT()} / {@link HeatHandler#deserializeNBT(net.minecraft.nbt.CompoundTag)}
 * are out of scope here.</p>
 */
class HeatHandlerTest {

    @Test
    void heatClamp_incrementPastMax_saturatesAtMax() {
        HeatHandler handler = new HeatHandler(100);
        handler.setHeat(90);

        handler.increment(20);

        assertEquals(100, handler.getHeat());
    }

    @Test
    void heatClamp_incrementToExactMax_reachesMax() {
        HeatHandler handler = new HeatHandler(100);
        handler.setHeat(90);

        handler.increment(10);

        assertEquals(100, handler.getHeat());
    }

    @Test
    void heatClamp_incrementInRange_addsExactly() {
        HeatHandler handler = new HeatHandler(100);
        handler.setHeat(50);

        handler.increment(20);

        assertEquals(70, handler.getHeat());
    }

    @Test
    void heatClamp_decrementBelowZero_saturatesAtZero() {
        HeatHandler handler = new HeatHandler(100);
        handler.setHeat(10);

        handler.decrement(20);

        assertEquals(0, handler.getHeat());
    }

    @Test
    void heatClamp_decrementToExactZero_reachesZero() {
        HeatHandler handler = new HeatHandler(100);
        handler.setHeat(10);

        handler.decrement(10);

        assertEquals(0, handler.getHeat());
    }

    @Test
    void heatClamp_decrementInRange_subtractsExactly() {
        HeatHandler handler = new HeatHandler(100);
        handler.setHeat(50);

        handler.decrement(20);

        assertEquals(30, handler.getHeat());
    }
}
