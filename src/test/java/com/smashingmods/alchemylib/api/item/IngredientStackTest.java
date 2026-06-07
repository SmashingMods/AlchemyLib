package com.smashingmods.alchemylib.api.item;

import com.smashingmods.alchemylib.testsupport.BootstrappedTest;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
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
 * Tests for {@link IngredientStack}: the constructor reads the access-transformer-opened
 * {@code Ingredient.values} field, so these need the Minecraft classpath and a populated item registry
 * (hence {@link BootstrappedTest}). All ingredients are built from vanilla {@link Items} and {@link ItemTags}.
 * They also pin the {@code toNetwork}/{@code fromNetwork} round trip -- the persistence seam the recipe packets
 * depend on -- and that {@code toStacks} does not mutate the Ingredient's shared cached stacks.
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
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        stack.toNetwork(buffer);
        return IngredientStack.fromNetwork(buffer);
    }
}
