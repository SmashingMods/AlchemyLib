package com.smashingmods.alchemylib.api.item;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An IngredientStack is similar to an {@link ItemStack}. It's a wrapper for an {@link Ingredient} with an
 * optional count. Ingredients extend Predicate&lt;ItemStack&gt; that hold values for potential items. For example, you can create an
 * Ingredient using {@link Ingredient#of(ItemLike...)}, {@link Ingredient#of(ItemStack...)}, or {@link Ingredient#of(TagKey)}.
 *
 * <p>IngredientStack has wrappers around these methods using constructors. You can use an IngredientStack the same way you would
 * use an Ingredient to test an ItemStack if it matches.</p>
 */
@SuppressWarnings("unused")
public class IngredientStack {

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
        this.registryName = new ResourceLocation(pIngredient.values[0].serialize().has("item") ?
                pIngredient.values[0].serialize().get("item").getAsString()
                : pIngredient.values[0].serialize().get("tag").getAsString());
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
     * Encodes this IngredientStack to a FriendlyByteBuf for transmitting over network.
     *
     * @param pBuffer {@link FriendlyByteBuf}
     */
    public void toNetwork(FriendlyByteBuf pBuffer) {
        ingredient.toNetwork(pBuffer);
        pBuffer.writeInt(count);
    }

    /**
     * This static method can be referenced when decoding a FriendlyByteBuf sent over the network
     * to create a new IngredientStack.
     *
     * @param pBuffer {@link FriendlyByteBuf}
     * @return IngredientStack
     */
    public static IngredientStack fromNetwork(FriendlyByteBuf pBuffer) {
        Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
        int count = pBuffer.readInt();
        return new IngredientStack(ingredient, count);
    }

    /**
     * Serializes this IngredientStack to a JsonObject and returns it.
     *
     * @return {@link JsonObject}
     */
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("ingredient", ingredient.toJson());
        json.addProperty("count", count);
        return json;
    }

    /**
     * This static method can be used to deserialize a JsonObject into an IngredientStack.
     *
     * @param pJson {@link JsonObject}
     * @return IngredientStack
     */
    public static IngredientStack fromJson(JsonObject pJson) {
        Ingredient ingredient = Ingredient.fromJson(pJson.getAsJsonObject("ingredient"));
        int count = GsonHelper.getAsInt(pJson, "count", 1);
        return new IngredientStack(ingredient, count);
    }

    /**
     * Convert this IngredientStack into a List of ItemStacks of each of the Ingredients items
     * with the count value set.
     *
     * @return List of ItemStacks.
     */
    public List<ItemStack> toStacks() {
        return Arrays.stream(ingredient.getItems())
                .peek(item -> item.setCount(count))
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
