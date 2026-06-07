package com.smashingmods.alchemylib.api.capability;

import com.smashingmods.alchemylib.testsupport.BootstrappedTest;
import net.minecraft.nbt.CompoundTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * NBT round-trip test for {@link HeatHandler}, which {@link HeatHandlerTest} leaves out of scope.
 *
 * <p>{@link CompoundTag} is plain NBT and does not itself need {@link net.minecraft.server.Bootstrap}, but
 * this runs under {@link BootstrappedTest} for a single shared bootstrap path across the storage/capability
 * tests added alongside it.</p>
 */
class HeatHandlerNbtTest extends BootstrappedTest {

    @Test
    void serializeThenDeserialize_roundTripsHeat() {
        HeatHandler source = new HeatHandler(100);
        source.setHeat(42);

        CompoundTag tag = source.serializeNBT();

        HeatHandler restored = new HeatHandler(100);
        restored.deserializeNBT(tag);

        assertEquals(42, restored.getHeat());
    }
}
