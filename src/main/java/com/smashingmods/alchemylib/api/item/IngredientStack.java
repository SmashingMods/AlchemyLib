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

    @Override
    public boolean equals(Object pObject) {
        if (this == pObject) return true;
        if (!(pObject instanceof IngredientStack that)) return false;
        if (getCount() != that.getCount()) return false;
        return getRegistryName().equals(that.getRegistryName());
    }

    @Override
    public int hashCode() {
        int result = getCount();
        result = 31 * result + getRegistryName().hashCode();
        return result;
    }

    public IngredientStack copy() {
        return new IngredientStack(ingredient, count);
    }
}
