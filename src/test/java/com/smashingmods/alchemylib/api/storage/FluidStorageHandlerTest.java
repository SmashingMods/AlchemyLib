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

    /**
     * Verifies the tank reads as 0 after a negative {@code setAmount}. This is jointly enforced by the
     * handler's {@code Math.max(..., 0)} ({@link FluidStorageHandler#setAmount}) and {@link FluidStack}'s own
     * {@code amount <= 0} -> empty flooring, so it does not isolate the handler's lower clamp — that clamp is
     * observably redundant with {@link FluidStack} on NeoForge 1.20.2. The handler's upper clamp is independently
     * covered by {@link #setAmount_aboveCapacity_clampsToCapacity()}.
     */
    @Test
    void setAmount_negative_readsAsZero() {
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

    /**
     * Verifies the tank reads as 0 after draining past empty. This is jointly enforced by the handler's
     * {@code Math.max(..., 0)} ({@link FluidStorageHandler#drainAmount}) and {@link FluidStack}'s own
     * {@code amount <= 0} -> empty flooring, so it does not isolate the handler's lower clamp — that clamp is
     * observably redundant with {@link FluidStack} on NeoForge 1.20.2. The handler's upper clamp is independently
     * covered by {@link #fillAmount_pastCapacity_clampsToCapacity()}.
     */
    @Test
    void drainAmount_pastZero_readsAsZero() {
        FluidStorageHandler tank = newTank();

        tank.drainAmount(100000);

        assertEquals(0, tank.getFluidAmount());
    }
}
