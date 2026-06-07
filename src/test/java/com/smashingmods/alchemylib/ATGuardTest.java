package com.smashingmods.alchemylib;

import net.minecraft.DetectedVersion;
import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tier-2 guard: proves AlchemyLib's access transformer (which exposes the otherwise-private
 * {@code Ingredient.values} field) is applied to the {@code test} classpath, not just {@code main}.
 *
 * <p>{@code Ingredient.values} is {@code private} in vanilla, so the {@link #valuesFieldIsAccessibleOnTestClasspath()}
 * body below only compiles if the AT made it public, and only runs without an {@link IllegalAccessError}
 * if that transformed {@code Ingredient} is on the test runtime classpath. {@link IngredientStack} reads
 * this same field in production, so this guard fails fast if the AT ever stops reaching {@code test}.</p>
 *
 * <p>Uses vanilla {@link Items#STONE} (registered by {@link Bootstrap#bootStrap()}) on purpose: constructing
 * a mod {@code Item} on 1.20.2 would trigger an intrusive-holder registry write that needs the registry
 * unfrozen, which this guard neither needs nor wants.</p>
 */
class ATGuardTest {

    @BeforeAll
    static void boot() {
        SharedConstants.setVersion(DetectedVersion.BUILT_IN);
        Bootstrap.bootStrap();
    }

    @Test
    void valuesFieldIsAccessibleOnTestClasspath() {
        Ingredient ing = Ingredient.of(Items.STONE);
        assertNotNull(ing.values, "Ingredient.values should be readable via the access transformer");
        assertTrue(ing.values.length >= 1, "Ingredient.of(Items.STONE) should hold at least one value");
    }
}
