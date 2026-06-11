package com.smashingmods.alchemylib.api.item;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

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

    /**
     * Stand-in registry name for an Ingredient that declares no values at all (e.g. {@code Ingredient.of()}). It is
     * namespaced to the mod so it can never collide with a real item or tag id, keeping {@link #getRegistryName()}
     * non-null for that degenerate case instead of indexing into an empty values array.
     */
    private static final ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath("alchemylib", "empty");

    /**
     * Stand-in registry name for a NeoForge {@linkplain Ingredient#isCustom() custom ingredient}
     * ({@link ICustomIngredient}). A custom ingredient declares no item or tag values -- its contents are only
     * knowable by resolving items, which the constructor must never do (see
     * {@link #IngredientStack(Ingredient, int)}) -- so this mod-namespaced stand-in keeps
     * {@link #getRegistryName()} non-null without colliding with a real item or tag id.
     */
    private static final ResourceLocation CUSTOM = ResourceLocation.fromNamespaceAndPath("alchemylib", "custom");

    private final Ingredient ingredient;
    private final int count;
    private final ResourceLocation registryName;
    /**
     * The equality key for this stack: the sorted, unmodifiable list of kind-prefixed declared value locations
     * ({@code "tag:<location>"} for tag values, {@code "item:<registry name>"} for item values) for a vanilla
     * ingredient, or the {@link ICustomIngredient} itself for a custom one. The kind prefix keeps a tag and an
     * item that share a location distinct -- with bare locations they compared equal and hash-based
     * recipe-input sets silently merged them. NeoForge requires custom ingredients to implement
     * {@code equals}/{@code hashCode} (its own, like {@code CompoundIngredient}, are records with structural
     * equality); a third-party custom that skips that contract degrades to instance identity, which still keeps
     * separately decoded ingredients distinct.
     */
    private final Object identity;

    /**
     * All other constructors reference this main constructor for creating a new IngredientStack.
     *
     * <p>Both {@link #registryName} and {@link #identity} are derived from what the Ingredient <em>declares</em>,
     * never from the items it resolves to: recipes are decoded during {@code RecipeManager.apply}, before registry
     * tags are bound to that reload, so {@link Ingredient#getItems()} would substitute -- and permanently memoize --
     * NeoForge's barrier "Empty Tag" placeholder stacks. A {@linkplain Ingredient#isCustom() custom ingredient}
     * gets the {@link #CUSTOM} stand-in name and is identified by its {@link ICustomIngredient}; a vanilla
     * ingredient is identified by its full declared value set (each value's location prefixed with its kind,
     * {@code tag:}/{@code item:}, sorted) with the first value's location as the representative
     * {@link #registryName}; an ingredient with no values at all (e.g. {@code Ingredient.of()}) gets the
     * {@link #EMPTY} stand-in and an empty identity.</p>
     *
     * @param pIngredient {@link Ingredient}
     * @param pCount The count for how items are in this stack. Only a max of 64 is valid, similar to ItemStack.
     */
    public IngredientStack(Ingredient pIngredient, int pCount) {
        this.ingredient = pIngredient;
        this.count = Math.min(pCount, 64);
        if (pIngredient.isCustom()) {
            this.identity = pIngredient.getCustomIngredient();
            this.registryName = CUSTOM;
        } else {
            this.identity = Arrays.stream(pIngredient.values)
                    .map(IngredientStack::valueIdentity)
                    .sorted()
                    .collect(Collectors.toUnmodifiableList());
            this.registryName = pIngredient.values.length > 0 ? valueLocation(pIngredient.values[0]) : EMPTY;
        }
    }

    /**
     * The identity entry an Ingredient value contributes: its {@linkplain #valueLocation(Ingredient.Value)
     * declared location} prefixed with the value's kind ({@code "tag:"} or {@code "item:"}). A tag and an item
     * may legally share a location, so the bare location is not enough to keep them distinct.
     *
     * @param pValue {@link Ingredient.Value}
     * @return the kind-prefixed declared location, never {@code null}
     */
    private static String valueIdentity(Ingredient.Value pValue) {
        String kind = pValue instanceof Ingredient.TagValue ? "tag" : "item";
        return kind + ":" + valueLocation(pValue);
    }

    /**
     * The location an Ingredient value declares: a tag value yields the tag's location, an item value yields the
     * item's registry name. Neither requires resolving the ingredient's items, so this is safe at recipe-decode
     * time.
     *
     * @param pValue {@link Ingredient.Value}
     * @return the declared {@link ResourceLocation}, never {@code null}
     */
    private static ResourceLocation valueLocation(Ingredient.Value pValue) {
        if (pValue instanceof Ingredient.TagValue tagValue) {
            return tagValue.tag().location();
        } else if (pValue instanceof Ingredient.ItemValue itemValue) {
            return BuiltInRegistries.ITEM.getKey(itemValue.item().getItem());
        }
        throw new IllegalArgumentException("Ingredient value is neither an item nor a tag value.");
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
     * Encodes this IngredientStack to a RegistryFriendlyByteBuf for transmitting over network.
     *
     * @param pBuffer {@link RegistryFriendlyByteBuf}
     */
    public void toNetwork(RegistryFriendlyByteBuf pBuffer) {
        Ingredient.CONTENTS_STREAM_CODEC.encode(pBuffer, ingredient);
        pBuffer.writeInt(count);
    }

    /**
     * This static method can be referenced when decoding a RegistryFriendlyByteBuf sent over the network
     * to create a new IngredientStack.
     *
     * @param pBuffer {@link RegistryFriendlyByteBuf}
     * @return IngredientStack
     */
    public static IngredientStack fromNetwork(RegistryFriendlyByteBuf pBuffer) {
        Ingredient ingredient = Ingredient.CONTENTS_STREAM_CODEC.decode(pBuffer);
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
        json.add("ingredient", Ingredient.CODEC_NONEMPTY.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow());
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
        Ingredient ingredient = Ingredient.CODEC_NONEMPTY.parse(JsonOps.INSTANCE, pJson.get("ingredient")).getOrThrow();
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
                .map(item -> {
                    ItemStack copy = item.copy();
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
     * Determines object equality of this IngredientStack against another object. Two IngredientStacks are equal
     * when they share the same {@link #getCount() count} and the same {@link #identity full ingredient identity},
     * so two multi-item ingredients that merely share a first item are not equal, and two custom ingredients are
     * only equal when their {@link ICustomIngredient}s are.
     *
     * @param pObject Object
     * @return boolean
     */
    @Override
    public boolean equals(Object pObject) {
        if (this == pObject) return true;
        if (!(pObject instanceof IngredientStack that)) return false;

        if (getCount() != that.getCount()) return false;
        return identity.equals(that.identity);
    }

    /**
     * Calculates the hash code for this IngredientStack based on its {@link #getCount() count} and its
     * {@link #identity full ingredient identity}.
     *
     * @return int
     */
    @Override
    public int hashCode() {
        int result = getCount();
        result = 31 * result + identity.hashCode();
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
