package com.smashingmods.alchemylib.api.item;

import com.smashingmods.alchemylib.testsupport.BootstrappedTest;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link IngredientStack}: the constructor reads the Ingredient's backing item/tag set, so these
 * need the Minecraft classpath and a populated item registry (hence {@link BootstrappedTest}). All ingredients
 * are built from vanilla {@link Items} and {@link ItemTags}. They also pin the {@code toNetwork}/
 * {@code fromNetwork} round trip -- the persistence seam the recipe packets depend on -- and that
 * {@code toStacks} does not mutate the Ingredient's shared cached backing.
 *
 * <p>The constructor's {@code orElseThrow} (an item-backed ingredient whose first item is not registered) is
 * unreachable for real ingredients -- an empty {@code Ingredient.of()} has an empty item list, so the first
 * lookup is an {@code IndexOutOfBoundsException} rather than that exception -- so it is left untested on
 * purpose.</p>
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

        assertEquals(ResourceLocation.fromNamespaceAndPath("minecraft", "stone"), stack.getRegistryName());
    }

    @Test
    void getRegistryName_tagValue_isTagLocation() {
        // 1.21.3 dropped Ingredient.of(TagKey); a tag-backed ingredient is built from a named HolderSet.
        // emptyNamed carries the tag key without binding the tag's contents, which is all the registry name
        // derivation reads.
        IngredientStack stack = new IngredientStack(Ingredient.of(HolderSet.emptyNamed(BuiltInRegistries.ITEM, ItemTags.PLANKS)));

        assertEquals(ResourceLocation.fromNamespaceAndPath("minecraft", "planks"), stack.getRegistryName());
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

    @Test
    void toStacks_appliesCountWithoutMutatingIngredientCache() {
        Ingredient ingredient = Ingredient.of(Items.STONE);
        // Ingredient#items streams the same cached Holder backing on every call; toStacks builds fresh
        // ItemStacks from it, so the count it applies must not leak back onto that shared backing.
        assertEquals(1, ingredient.items().count());

        IngredientStack stack = new IngredientStack(ingredient, 16);
        List<ItemStack> stacks = stack.toStacks();

        assertEquals(1, stacks.size());
        assertEquals(16, stacks.get(0).getCount());
        // The returned stack carries the count; the Ingredient's cached backing is untouched -- it still
        // resolves to the single STONE holder, and a fresh toStacks call produces an independent stack.
        assertEquals(1, ingredient.items().count());
        assertEquals(Items.STONE, ingredient.items().findFirst().orElseThrow().value());
        assertEquals(16, stack.toStacks().get(0).getCount());
    }

    @Test
    void networkRoundTrip_itemBacked_reproducesIngredientAndCount() {
        // Only the network seam is exercised here. The JSON seam (toJson/fromJson) routes through
        // Ingredient.CODEC, which is NeoForge's dispatch codec keyed on the neoforge:ingredient_serializer
        // registry -- absent under a bare Bootstrap -- so toJson throws here and is left to the recipe gametests.
        IngredientStack original = new IngredientStack(Ingredient.of(Items.STONE), 16);

        IngredientStack decoded = encodeDecodeNetwork(original);

        assertEquals(original, decoded);
        assertEquals(original.getRegistryName(), decoded.getRegistryName());
        assertEquals(16, decoded.getCount());
    }

    private static IngredientStack encodeDecodeNetwork(IngredientStack stack) {
        RegistryFriendlyByteBuf buffer = registryBuffer();
        stack.toNetwork(buffer);
        return IngredientStack.fromNetwork(buffer);
    }
}
