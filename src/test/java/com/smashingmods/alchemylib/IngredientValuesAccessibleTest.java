package com.smashingmods.alchemylib;

import com.smashingmods.alchemylib.testsupport.BootstrappedTest;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Guard: confirms the access-transformer-opened {@code Ingredient.values} field is readable on the
 * {@code test} classpath. {@link IngredientStack} reads this same field in production, so this guard
 * fails fast if {@code Ingredient.values} ever stops being accessible to the tests.
 *
 * <p>{@code Ingredient.values} is {@code private} in vanilla, so the
 * {@link #ingredientValuesFieldIsAccessibleOnTestClasspath()} body below only compiles once an AT has
 * made it public, and only runs without an {@link IllegalAccessError} if that transformed
 * {@code Ingredient} is on the test runtime classpath.</p>
 *
 * <p>NOTE: on NeoForge 1.20.2 this field is opened by NeoForge's own bundled access transformer.
 * AlchemyLib's {@code accesstransformer.cfg} carries an identical line for it, but that line is
 * redundant on this version, so this test does <em>not</em> (and cannot) discriminate AlchemyLib's
 * AT specifically -- it would pass green even with AlchemyLib's AT removed. It claims only that the
 * field is accessible, which is what {@link IngredientStack} requires.</p>
 *
 * <p>Uses vanilla {@link Items#STONE} (registered by {@link Bootstrap#bootStrap()}) on purpose: constructing
 * a mod {@code Item} on 1.20.2 would trigger an intrusive-holder registry write that needs the registry
 * unfrozen, which this guard neither needs nor wants.</p>
 */
class IngredientValuesAccessibleTest extends BootstrappedTest {

    @Test
    void ingredientValuesFieldIsAccessibleOnTestClasspath() {
        Ingredient ing = Ingredient.of(Items.STONE);
        assertNotNull(ing.values, "Ingredient.values should be readable via the access transformer");
        assertTrue(ing.values.length >= 1, "Ingredient.of(Items.STONE) should hold at least one value");
    }
}
