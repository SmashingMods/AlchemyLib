package com.smashingmods.alchemylib.api.storage;

import com.smashingmods.alchemylib.testsupport.BootstrappedTest;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link ProcessingSlotHandler}'s slot count helpers. {@link ItemStack} reads the item
 * registry for its max-stack size, so these need a populated registry (hence {@link BootstrappedTest}); all
 * stacks are vanilla {@link Items#STONE}, whose max stack size is 64.
 */
class ProcessingSlotHandlerTest extends BootstrappedTest {

    @Test
    void incrementSlot_withinMaxStack_addsAmount() {
        ProcessingSlotHandler handler = new ProcessingSlotHandler(2);
        handler.setStackInSlot(0, new ItemStack(Items.STONE, 1));

        handler.incrementSlot(0, 10);

        assertEquals(11, handler.getStackInSlot(0).getCount());
    }

    @Test
    void incrementSlot_pastMaxStack_isNoOp() {
        ProcessingSlotHandler handler = new ProcessingSlotHandler(2);
        handler.setStackInSlot(0, new ItemStack(Items.STONE, 60));

        // 60 + 10 = 70 > 64 (max stack size), so the guard short-circuits and the count is left untouched.
        handler.incrementSlot(0, 10);

        assertEquals(60, handler.getStackInSlot(0).getCount());
    }

    @Test
    void setOrIncrement_emptySlot_setsStack() {
        ProcessingSlotHandler handler = new ProcessingSlotHandler(2);

        handler.setOrIncrement(0, new ItemStack(Items.STONE, 5));

        assertEquals(5, handler.getStackInSlot(0).getCount());
    }

    @Test
    void setOrIncrement_occupiedSlot_incrementsStack() {
        ProcessingSlotHandler handler = new ProcessingSlotHandler(2);
        handler.setStackInSlot(0, new ItemStack(Items.STONE, 5));

        handler.setOrIncrement(0, new ItemStack(Items.STONE, 3));

        assertEquals(8, handler.getStackInSlot(0).getCount());
    }

    @Test
    void decrementSlot_aboveAmount_subtractsAmount() {
        ProcessingSlotHandler handler = new ProcessingSlotHandler(2);
        handler.setStackInSlot(0, new ItemStack(Items.STONE, 10));

        handler.decrementSlot(0, 4);

        assertEquals(6, handler.getStackInSlot(0).getCount());
    }

    @Test
    void decrementSlot_toZero_emptiesSlot() {
        ProcessingSlotHandler handler = new ProcessingSlotHandler(2);
        handler.setStackInSlot(0, new ItemStack(Items.STONE, 6));

        handler.decrementSlot(0, 6);

        assertTrue(handler.getStackInSlot(0).isEmpty());
    }

    @Test
    void decrementSlot_belowZero_isNoOp() {
        ProcessingSlotHandler handler = new ProcessingSlotHandler(2);
        handler.setStackInSlot(0, new ItemStack(Items.STONE, 3));

        // 3 - 5 < 0, so the guard returns early and the count is left untouched.
        handler.decrementSlot(0, 5);

        assertEquals(3, handler.getStackInSlot(0).getCount());
    }

    @Test
    void isEmpty_freshHandler_isTrue() {
        ProcessingSlotHandler handler = new ProcessingSlotHandler(2);

        assertTrue(handler.isEmpty());
    }

    @Test
    void isEmpty_afterSettingStack_isFalse() {
        ProcessingSlotHandler handler = new ProcessingSlotHandler(2);
        handler.setStackInSlot(0, new ItemStack(Items.STONE, 1));

        assertFalse(handler.isEmpty());
    }
}
