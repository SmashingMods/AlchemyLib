package com.smashingmods.alchemylib.api.item;

import com.smashingmods.alchemylib.testsupport.BootstrappedTest;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link IngredientStack}: the constructor reads the access-transformer-opened
 * {@code Ingredient.values} field, so these need the Minecraft classpath and a populated item registry
 * (hence {@link BootstrappedTest}). All ingredients are built from vanilla {@link Items} and {@link ItemTags}
 * (plus NeoForge's {@link CompoundIngredient} for the custom-ingredient paths).
 * They also pin the {@code toNetwork}/{@code fromNetwork} round trip -- the persistence seam the recipe packets
 * depend on -- and that {@code toStacks} does not mutate the Ingredient's shared cached stacks.
 *
 * <p>NeoForge custom ingredients and the empty {@code Ingredient.of()} both have an empty {@code values} array,
 * so the constructor must not read {@code values[0]} blindly -- doing so threw an
 * {@code ArrayIndexOutOfBoundsException} while recipes were being decoded. Nor may it fall back to resolving
 * {@code Ingredient.getItems()}: at decode time registry tags are not yet bound, so that would memoize NeoForge's
 * barrier "Empty Tag" placeholders. Both shapes instead map to mod-namespaced stand-in registry names, pinned
 * below. The {@code throw IllegalArgumentException} branch (a declared value that is neither item nor tag)
 * stays unreachable for real ingredients, so it is left untested on purpose.</p>
 *
 * <p>Equality is keyed on the count plus the Ingredient's full identity (the declared tag locations and sorted
 * item registry names, or the {@code ICustomIngredient} itself), so the multi-item cases below pin that two
 * ingredients sharing only a first item are not equal while the same item set in any order is, and the custom
 * cases pin that two different custom ingredients stay distinct -- with first-value-only equality they collapsed
 * to one stand-in name and hash-based recipe-input sets silently dropped inputs.</p>
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
        IngredientStack stack = new IngredientStack(Ingredient.of(ItemTags.PLANKS));

        assertEquals(ResourceLocation.fromNamespaceAndPath("minecraft", "planks"), stack.getRegistryName());
    }

    @Test
    void constructor_emptyIngredient_doesNotThrow() {
        // Regression for the ArrayIndexOutOfBoundsException at IngredientStack.<init>: an empty Ingredient has an
        // empty values[] array (the shape NeoForge custom ingredients also take), which crashed recipe decode.
        assertDoesNotThrow(() -> new IngredientStack(Ingredient.of()));
    }

    @Test
    void getRegistryName_emptyIngredient_isEmptyStandIn() {
        // Pins the same empty-values shape as the no-throw test above: the registry name must be the
        // mod-namespaced stand-in, not anything resolved through getItems() at decode time.
        IngredientStack stack = new IngredientStack(Ingredient.of());

        assertEquals(ResourceLocation.fromNamespaceAndPath("alchemylib", "empty"), stack.getRegistryName());
    }

    @Test
    void constructor_customIngredient_doesNotThrow() {
        // Regression for the ArrayIndexOutOfBoundsException at IngredientStack.<init> (#12): a NeoForge custom
        // ingredient constructs with values = new Value[0], the exact shape that crashed world creation in packs
        // whose recipes use compound/custom ingredients.
        assertDoesNotThrow(() -> new IngredientStack(compoundOf(Items.STONE, Items.DIRT)));
    }

    @Test
    void getRegistryName_customIngredient_isCustomStandIn() {
        IngredientStack stack = new IngredientStack(compoundOf(Items.STONE, Items.DIRT));

        assertEquals(ResourceLocation.fromNamespaceAndPath("alchemylib", "custom"), stack.getRegistryName());
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
    void equals_multiItemSharingFirstItem_areNotEqual() {
        // Identity must reflect the whole declared value set, not just the first item: these two share IRON_INGOT
        // as their first item but differ on the second, so they must not collide in hash-based dedup/lookup.
        IngredientStack first = new IngredientStack(Ingredient.of(Items.IRON_INGOT, Items.GOLD_INGOT), 4);
        IngredientStack second = new IngredientStack(Ingredient.of(Items.IRON_INGOT, Items.COPPER_INGOT), 4);

        assertNotEquals(first, second);
    }

    @Test
    void equalsAndHashCode_sameItemSetAndCount_areEqual() {
        // Same full item set (regardless of declared order) and same count: equal, with matching hash codes.
        IngredientStack first = new IngredientStack(Ingredient.of(Items.IRON_INGOT, Items.GOLD_INGOT), 4);
        IngredientStack second = new IngredientStack(Ingredient.of(Items.GOLD_INGOT, Items.IRON_INGOT), 4);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equalsAndHashCode_structurallySameCustomIngredients_areEqual() {
        // Custom ingredients are identified by their ICustomIngredient. NeoForge's CompoundIngredient is a record,
        // so two separately built but structurally identical compounds compare equal.
        IngredientStack first = new IngredientStack(compoundOf(Items.STONE, Items.DIRT), 4);
        IngredientStack second = new IngredientStack(compoundOf(Items.STONE, Items.DIRT), 4);

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void equals_differentCustomIngredients_areNotEqual() {
        IngredientStack first = new IngredientStack(compoundOf(Items.IRON_INGOT, Items.GOLD_INGOT), 4);
        IngredientStack second = new IngredientStack(compoundOf(Items.STONE, Items.DIRT), 4);

        assertNotEquals(first, second);
    }

    @Test
    void linkedHashSet_differentCustomIngredients_keepsBoth() {
        // Recipe-input-drop regression: recipes collect their inputs into a LinkedHashSet<IngredientStack>. With
        // equality keyed on a single resolved name, every unresolvable custom ingredient collapsed to the same
        // stand-in and the set silently dropped all but the first input of such a recipe.
        Set<IngredientStack> inputs = new LinkedHashSet<>();
        inputs.add(new IngredientStack(compoundOf(Items.IRON_INGOT, Items.GOLD_INGOT), 4));
        inputs.add(new IngredientStack(compoundOf(Items.STONE, Items.DIRT), 4));

        assertEquals(2, inputs.size());
    }

    @Test
    void toStacks_appliesCountWithoutMutatingIngredientCache() {
        Ingredient ingredient = Ingredient.of(Items.STONE);
        // Ingredient#getItems caches and hands back the same ItemStack instances on every call, so a stack
        // count of 1 here is the shared cache that toStacks must not write through.
        ItemStack cached = ingredient.getItems()[0];
        assertEquals(1, cached.getCount());

        IngredientStack stack = new IngredientStack(ingredient, 16);
        List<ItemStack> stacks = stack.toStacks();

        assertEquals(1, stacks.size());
        assertEquals(16, stacks.get(0).getCount());
        // The returned stack carries the count; the Ingredient's cached stack stays at 1 (toStacks copies
        // rather than setting the count on the shared instance).
        assertEquals(1, cached.getCount());
        assertEquals(1, ingredient.getItems()[0].getCount());
    }

    @Test
    void networkRoundTrip_itemBacked_reproducesIngredientAndCount() {
        // Only the network seam is exercised here. The JSON seam (toJson/fromJson) routes through
        // Ingredient.CODEC_NONEMPTY, which is NeoForge's dispatch codec keyed on the neoforge:ingredient_serializer
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

    /**
     * A real NeoForge custom ingredient: {@link CompoundIngredient#of} with two children wraps a
     * {@code CompoundIngredient} in a vanilla {@code Ingredient} whose {@code values} array is empty and whose
     * {@code isCustom()} is true -- the exact shape recipe decode hands to the IngredientStack constructor.
     * Only construction is exercised here; serializing it would need the neoforge:ingredient_type registry,
     * which the bare bootstrap does not populate.
     */
    private static Ingredient compoundOf(ItemLike pFirst, ItemLike pSecond) {
        return CompoundIngredient.of(Ingredient.of(pFirst), Ingredient.of(pSecond));
    }
}
