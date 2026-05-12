package com.smashingmods.alchemylib.api.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An IngredientStack pairs an {@link Ingredient} with a count. It mirrors {@link ItemStack}
 * but uses a predicate-based ingredient so it can match by item or tag.
 *
 * <p>Serialization uses {@link #CODEC} for data and {@link #STREAM_CODEC} for network.
 */
@SuppressWarnings("unused")
public class IngredientStack {

    public static final Codec<IngredientStack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(IngredientStack::getIngredient),
            Codec.INT.optionalFieldOf("count", 1).forGetter(IngredientStack::getCount)
    ).apply(instance, IngredientStack::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, IngredientStack> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, IngredientStack::getIngredient,
            ByteBufCodecs.VAR_INT, IngredientStack::getCount,
            IngredientStack::new
    );

    private final Ingredient ingredient;
    private final int count;
    private final ResourceLocation registryName;

    /**
     * All other constructors reference this main constructor for creating a new IngredientStack.
     *
     * <p>{@link IngredientStack#registryName} is set by creating a new {@link ResourceLocation} from the 0th
     * entry of the Ingredient's values array. The array is first serialized and then either the "item" or "tag" value
     * is retrieved depending on which exists.</p>
     *
     * @param pIngredient {@link Ingredient}
     * @param pCount The count for how items are in this stack. Only a max of 64 is valid, similar to ItemStack.
     */
    public IngredientStack(Ingredient pIngredient, int pCount) {
        this.ingredient = pIngredient;
        this.count = Math.min(pCount, 64);
        // Resolve a registry-name from the first matching item. For tag-based ingredients
        // this picks the first item in the tag; consumers that need the original tag id
        // should retain it externally.
        ItemStack[] items = pIngredient.getItems();
        if (items.length > 0 && !items[0].isEmpty()) {
            this.registryName = BuiltInRegistries.ITEM.getKey(items[0].getItem());
        } else {
            this.registryName = ResourceLocation.parse("minecraft:air");
        }
    }

    public IngredientStack(Ingredient pIngredient) {
        this(pIngredient, 1);
    }

    public IngredientStack(ItemStack pItemStack) {
        this(Ingredient.of(pItemStack.getItem()), pItemStack.getCount());
    }

    public IngredientStack(ItemStack pItemStack, int pCount) {
        this(Ingredient.of(pItemStack.getItem()), pCount);
    }

    public IngredientStack(ItemLike pItemLike, int pCount) {
        this(Ingredient.of(pItemLike), pCount);
    }

    public IngredientStack(ItemLike pItemLike) {
        this(Ingredient.of(pItemLike));
    }

    /**
     * Convert this IngredientStack into a list of ItemStacks, one per matching ingredient item,
     * each with this stack's count.
     */
    public List<ItemStack> toStacks() {
        return Arrays.stream(ingredient.getItems())
                .map(stack -> {
                    ItemStack copy = stack.copy();
                    copy.setCount(count);
                    return copy;
                })
                .collect(Collectors.toList());
    }

    /**
     * Pass-through for the Ingredient's test method. This is used to determine if an ItemStack matches the predicate of the
     * Ingredient. For example, if an Ingredient was made using the tag key "forge:chests/wooden" and you tested an item
     * with the Resource Location "minecraft:chest", then it would match because "minecraft:chest" is contained within that tag.
     *
     * @param pItemStack {@link ItemStack} to test against.
     */
    public boolean matches(ItemStack pItemStack) {
        return ingredient.test(pItemStack);
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public int getCount() {
        return count;
    }

    public boolean isEmpty() {
        return ingredient.isEmpty();
    }

    /**
     * Determines object equality of this IngredientStack against another object based on {@link ResourceLocation#equals(Object)}.
     *
     * @param pObject Object
     * @return boolean
     */
    @Override
    public boolean equals(Object pObject) {
        if (this == pObject) return true;
        if (!(pObject instanceof IngredientStack that)) return false;
        if (getCount() != that.getCount()) return false;
        return getRegistryName().equals(that.getRegistryName());
    }

    /**
     * Calculates the hash code for this IngredientStack based on its {@link ResourceLocation registryName} hash code.
     *
     * @return int
     */
    @Override
    public int hashCode() {
        int result = getCount();
        result = 31 * result + getRegistryName().hashCode();
        return result;
    }

    /**
     * Creates a new copy of this IngredientStack, useful for when you need to modify an IngredientStack
     * but don't want changes to cascade to other objects referencing this IngredientStack.
     *
     * @return IngredientStack
     */
    public IngredientStack copy() {
        return new IngredientStack(ingredient, count);
    }
}
