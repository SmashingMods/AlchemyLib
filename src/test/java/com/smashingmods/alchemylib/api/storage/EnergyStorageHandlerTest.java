package com.smashingmods.alchemylib.api.storage;

import com.smashingmods.alchemylib.testsupport.BootstrappedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link EnergyStorageHandler}'s energy helpers and their clamping against a 1000 capacity.
 *
 * <p>This is pure int arithmetic and does not itself need {@link net.minecraft.server.Bootstrap}, but it runs
 * under {@link BootstrappedTest} with the other storage-handler tests for a single shared bootstrap path.</p>
 */
class EnergyStorageHandlerTest extends BootstrappedTest {

    @Test
    void setEnergy_withinRange_setsExactly() {
        EnergyStorageHandler handler = new EnergyStorageHandler(1000);

        handler.setEnergy(500);

        assertEquals(500, handler.getEnergyStored());
    }

    @Test
    void setEnergy_aboveCapacity_clampsToCapacity() {
        EnergyStorageHandler handler = new EnergyStorageHandler(1000);

        handler.setEnergy(2000);

        assertEquals(1000, handler.getEnergyStored());
    }

    @Test
    void setEnergy_negative_clampsToZero() {
        EnergyStorageHandler handler = new EnergyStorageHandler(1000);

        handler.setEnergy(-50);

        assertEquals(0, handler.getEnergyStored());
    }

    @Test
    void addEnergy_withinRange_addsAmount() {
        EnergyStorageHandler handler = new EnergyStorageHandler(1000);
        handler.setEnergy(500);

        handler.addEnergy(300);

        assertEquals(800, handler.getEnergyStored());
    }

    @Test
    void addEnergy_pastCapacity_clampsToCapacity() {
        EnergyStorageHandler handler = new EnergyStorageHandler(1000);
        handler.setEnergy(500);

        handler.addEnergy(100000);

        assertEquals(1000, handler.getEnergyStored());
    }

    @Test
    void consumeEnergy_withinRange_subtractsAmount() {
        EnergyStorageHandler handler = new EnergyStorageHandler(1000);
        handler.setEnergy(500);

        handler.consumeEnergy(200);

        assertEquals(300, handler.getEnergyStored());
    }

    @Test
    void consumeEnergy_pastZero_clampsToZero() {
        EnergyStorageHandler handler = new EnergyStorageHandler(1000);
        handler.setEnergy(500);

        handler.consumeEnergy(100000);

        assertEquals(0, handler.getEnergyStored());
    }
}
