package com.smashingmods.alchemylib.api.storage;

import com.smashingmods.alchemylib.testsupport.BootstrappedTest;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tier-2 tests for {@link FluidStorageHandler}'s amount helpers and their clamping. {@link FluidStack}
 * resolves its fluid against the fluid registry, so these need a populated registry (hence
 * {@link BootstrappedTest}); the tank holds vanilla {@link Fluids#WATER} with a 10000 capacity, seeded to 5000.
 */
class FluidStorageHandlerTest extends BootstrappedTest {

    private static FluidStorageHandler newTank() {
        return new FluidStorageHandler(10000, new FluidStack(Fluids.WATER, 5000));
    }

    @Test
    void setAmount_withinRange_setsExactly() {
        FluidStorageHandler tank = newTank();

        tank.setAmount(2000);

        assertEquals(2000, tank.getFluidAmount());
    }

    @Test
    void setAmount_aboveCapacity_clampsToCapacity() {
        FluidStorageHandler tank = newTank();

        tank.setAmount(20000);

        assertEquals(10000, tank.getFluidAmount());
    }

    @Test
    void setAmount_negative_clampsToZero() {
        FluidStorageHandler tank = newTank();

        tank.setAmount(-50);

        assertEquals(0, tank.getFluidAmount());
    }

    @Test
    void fillAmount_withinRange_addsAmount() {
        FluidStorageHandler tank = newTank();

        tank.fillAmount(3000);

        assertEquals(8000, tank.getFluidAmount());
    }

    @Test
    void fillAmount_pastCapacity_clampsToCapacity() {
        FluidStorageHandler tank = newTank();

        tank.fillAmount(100000);

        assertEquals(10000, tank.getFluidAmount());
    }

    @Test
    void drainAmount_withinRange_subtractsAmount() {
        FluidStorageHandler tank = newTank();

        tank.drainAmount(2000);

        assertEquals(3000, tank.getFluidAmount());
    }

    @Test
    void drainAmount_pastZero_clampsToZero() {
        FluidStorageHandler tank = newTank();

        tank.drainAmount(100000);

        assertEquals(0, tank.getFluidAmount());
    }
}
