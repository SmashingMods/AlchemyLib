package com.smashingmods.alchemylib.api.item;

import com.smashingmods.alchemylib.testsupport.BootstrappedTest;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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
 * Tests for {@link IngredientStack}: the constructor reads the Ingredient's backing item/tag set, so these
 * need the Minecraft classpath and a populated item registry (hence {@link BootstrappedTest}). All ingredients
 * are built from vanilla {@link Items} and {@link ItemTags} (plus NeoForge's {@link CompoundIngredient} for the
 * custom-ingredient paths). They also pin the {@code toNetwork}/{@code fromNetwork} round trip -- the
 * persistence seam the recipe packets depend on -- and that {@code toStacks} does not mutate the Ingredient's
 * shared cached backing.
 *
 * <p>A NeoForge {@code Ingredient.isCustom()} ingredient has no backing item/tag set at all --
 * {@code Ingredient.getValues()} throws {@code IllegalStateException} for it -- so the constructor must branch
 * on {@code isCustom()} before unwrapping values; unguarded, every compound/custom ingredient in a machine
 * recipe crashed world creation during recipe decode. Nor may it fall back to resolving {@code Ingredient.items()}:
 * at decode time registry tags are not yet bound. Customs instead map to a mod-namespaced stand-in registry
 * name, pinned below.</p>
 *
 * <p>Equality is keyed on the count plus the Ingredient's full identity (the declared tag location or item
 * registry names, each prefixed with its kind -- {@code tag:}/{@code item:} -- and sorted, or the
 * {@code ICustomIngredient} itself), so the multi-item cases below pin that two ingredients sharing only a first
 * item are not equal while the same item set in any order is, the tag-vs-item case pins that a tag and an item
 * sharing a location stay distinct, and the custom cases pin that two different custom ingredients stay distinct
 * -- collapsing any of these to one key would make hash-based recipe-input sets silently drop inputs. The degenerate empty {@code Ingredient.of()} (no
 * items, so no first registry name) falls back to a stand-in location rather than throwing, but that path needs
 * no real ingredient and is left untested.</p>
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
    void constructor_customIngredient_doesNotThrow() {
        // Regression for the IllegalStateException ("Cannot retrieve values from custom ingredient!") at
        // IngredientStack.<init>: a NeoForge custom ingredient throws from getValues(), the exact shape that
        // crashed world creation in packs whose machine recipes use compound/custom ingredients.
        assertDoesNotThrow(() -> new IngredientStack(compoundOf(Items.STONE, Items.DIRT)));
    }

    @Test
    void getRegistryName_customIngredient_isCustomStandIn() {
        IngredientStack stack = new IngredientStack(compoundOf(Items.STONE, Items.DIRT));

        assertEquals(ResourceLocation.fromNamespaceAndPath("alchemylib", "custom"), stack.getRegistryName());
    }

    @Test
    void isEmpty_customIngredient_isFalse() {
        // isEmpty must short-circuit on isCustom() before reading getValues() (which throws for customs), and a
        // custom ingredient is never empty. It must not delegate to Ingredient.isEmpty() either: NeoForge's
        // patched isEmpty() resolves the custom ingredient's items, which is unsafe at recipe-decode time.
        IngredientStack stack = new IngredientStack(compoundOf(Items.STONE, Items.DIRT));

        assertFalse(stack.isEmpty());
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
    void equals_tagAndItemSharingLocation_areNotEqual() {
        // Kind-discriminator regression: a tag and an item may legally share a ResourceLocation. Identity entries
        // are kind-prefixed ("tag:"/"item:") so these two must not compare equal -- with bare locations they
        // collided and hash-based recipe-input sets silently merged distinct inputs. emptyNamed carries the tag
        // key without binding the tag's contents, which is all the identity derivation reads.
        TagKey<Item> stoneTag = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("minecraft", "stone"));
        IngredientStack tagBacked = new IngredientStack(Ingredient.of(HolderSet.emptyNamed(BuiltInRegistries.ITEM, stoneTag)), 4);
        IngredientStack itemBacked = new IngredientStack(Ingredient.of(Items.STONE), 4);

        assertNotEquals(tagBacked, itemBacked);
    }

    @Test
    void equals_multiItemSharingFirstItem_areNotEqual() {
        // Identity must reflect the whole item set, not just the first item: these two share IRON_INGOT as their
        // first item but differ on the second, so they must not collide in hash-based dedup/lookup.
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
        // Recipe-input-drop regression: recipes collect their inputs into a LinkedHashSet<IngredientStack>. If
        // equality were keyed on the stand-in registry name alone, every custom ingredient would collapse to the
        // same key and the set would silently drop all but the first input of such a recipe.
        Set<IngredientStack> inputs = new LinkedHashSet<>();
        inputs.add(new IngredientStack(compoundOf(Items.IRON_INGOT, Items.GOLD_INGOT), 4));
        inputs.add(new IngredientStack(compoundOf(Items.STONE, Items.DIRT), 4));

        assertEquals(2, inputs.size());
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

    /**
     * A real NeoForge custom ingredient: {@link CompoundIngredient#of} with two children wraps a
     * {@code CompoundIngredient} in a vanilla {@code Ingredient} whose {@code isCustom()} is true and whose
     * {@code getValues()} throws -- the exact shape recipe decode hands to the IngredientStack constructor.
     * Only construction is exercised here; serializing it would need the neoforge:ingredient_type registry,
     * which the bare bootstrap does not populate.
     */
    private static Ingredient compoundOf(ItemLike pFirst, ItemLike pSecond) {
        return CompoundIngredient.of(Ingredient.of(pFirst), Ingredient.of(pSecond));
    }
}
