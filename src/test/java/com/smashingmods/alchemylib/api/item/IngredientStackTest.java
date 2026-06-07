package com.smashingmods.alchemylib.api.item;

import com.smashingmods.alchemylib.testsupport.BootstrappedTest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tier-2 tests for {@link IngredientStack}: the constructor reads the access-transformer-opened
 * {@code Ingredient.values} field, so these need the Minecraft classpath and a populated item registry
 * (hence {@link BootstrappedTest}). All ingredients are built from vanilla {@link Items} and {@link ItemTags}.
 *
 * <p>The constructor's {@code else throw IllegalArgumentException} branch (value neither item nor tag) is
 * unreachable for real ingredients -- an empty {@code Ingredient.of()} has an empty {@code values} array, so
 * {@code values[0]} is an {@code ArrayIndexOutOfBoundsException} rather than that exception -- so it is left
 * untested on purpose.</p>
 */
class IngredientStackTest extends BootstrappedTest {

    @Test
    void constructor_countAboveSixtyFour_clampsToSixtyFour() {
        IngredientStack stack = new IngredientStack(Ingredient.of(Items.STONE), 100);

        assertEquals(64, stack.getCount());
    }

    @Test
    void matches_sameItem_isTrue() {
        IngredientStack stack = new IngredientStack(Ingredient.of(Items.STONE));

        assertTrue(stack.matches(new ItemStack(Items.STONE)));
    }

    @Test
    void matches_differentItem_isFalse() {
        IngredientStack stack = new IngredientStack(Ingredient.of(Items.STONE));

        assertFalse(stack.matches(new ItemStack(Items.DIRT)));
    }

    @Test
    void getRegistryName_itemValue_isItemRegistryName() {
        IngredientStack stack = new IngredientStack(Ingredient.of(Items.STONE));

        assertEquals(new ResourceLocation("minecraft", "stone"), stack.getRegistryName());
    }

    @Test
    void getRegistryName_tagValue_isTagLocation() {
        IngredientStack stack = new IngredientStack(Ingredient.of(ItemTags.PLANKS));

        assertEquals(new ResourceLocation("minecraft", "planks"), stack.getRegistryName());
    }

    @Test
    void equalsAndHashCode_sameCountAndRegistryName_areEqual() {
        IngredientStack first = new IngredientStack(Ingredient.of(Items.STONE), 4);
        IngredientStack second = new IngredientStack(Ingredient.of(Items.STONE), 4);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equals_differentCount_areNotEqual() {
        IngredientStack first = new IngredientStack(Ingredient.of(Items.STONE), 4);
        IngredientStack second = new IngredientStack(Ingredient.of(Items.STONE), 8);

        assertNotEquals(first, second);
    }

    @Test
    void equals_differentRegistryName_areNotEqual() {
        IngredientStack first = new IngredientStack(Ingredient.of(Items.STONE), 4);
        IngredientStack second = new IngredientStack(Ingredient.of(Items.DIRT), 4);

        assertNotEquals(first, second);
    }
}
