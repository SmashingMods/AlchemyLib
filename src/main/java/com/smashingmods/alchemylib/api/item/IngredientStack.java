package com.smashingmods.alchemylib.api.item;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * An IngredientStack is similar to an {@link ItemStack}. It's a wrapper for an {@link Ingredient} with an
 * optional count. Ingredients extend Predicate&lt;ItemStack&gt; that hold values for potential items. For example, you can create an
 * Ingredient using {@link Ingredient#of(ItemLike...)} for items, or {@link Ingredient#of(net.minecraft.core.HolderSet)} for a tag's items.
 *
 * <p>IngredientStack has wrappers around these methods using constructors. You can use an IngredientStack the same way you would
 * use an Ingredient to test an ItemStack if it matches.</p>
 */
@SuppressWarnings("unused")
public class IngredientStack {

    /**
     * Stand-in registry name for an item-backed Ingredient with no items (e.g. {@code Ingredient.of()}). It is
     * namespaced to the mod so it can never collide with a real item or tag id, keeping {@link #getRegistryName()}
     * non-null for that degenerate case instead of indexing into an empty holder list.
     */
    private static final ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath("alchemylib", "empty");

    /**
     * Stand-in registry name for a NeoForge {@linkplain Ingredient#isCustom() custom ingredient}
     * ({@link ICustomIngredient}). A custom ingredient declares no item or tag set --
     * {@link Ingredient#getValues()} throws {@link IllegalStateException} for it -- and its contents are only
     * knowable by resolving items, which the constructor must never do (see
     * {@link #IngredientStack(Ingredient, int)}) -- so this mod-namespaced stand-in keeps
     * {@link #getRegistryName()} non-null without colliding with a real item or tag id.
     */
    private static final ResourceLocation CUSTOM = ResourceLocation.fromNamespaceAndPath("alchemylib", "custom");

    private final Ingredient ingredient;
    private final int count;
    private final ResourceLocation registryName;
    /**
     * The equality key for this stack: a sorted, unmodifiable list of kind-prefixed declared locations
     * ({@code "tag:<location>"} for a tag-backed ingredient, {@code "item:<registry name>"} per item for an
     * item-backed one) for a vanilla ingredient, or the {@link ICustomIngredient} itself for a custom one. The
     * kind prefix keeps a tag and an item that share a location distinct -- with bare locations they compared
     * equal and hash-based recipe-input sets silently merged them. NeoForge requires custom ingredients to
     * implement {@code equals}/{@code hashCode} (its own, like {@code CompoundIngredient}, are records with
     * structural equality); a third-party custom that skips that contract degrades to instance identity, which
     * still keeps separately decoded ingredients distinct.
     */
    private final Object identity;

    /**
     * All other constructors reference this main constructor for creating a new IngredientStack.
     *
     * <p>Both {@link #registryName} and {@link #identity} are derived from what the Ingredient <em>declares</em>,
     * never from the items it resolves to: recipes are decoded during {@code RecipeManager.apply}, before registry
     * tags are bound to that reload, so the constructor must not call {@link Ingredient#items()}. A
     * {@linkplain Ingredient#isCustom() custom ingredient} declares no item or tag set at all -- its
     * {@link Ingredient#getValues()} throws -- so it gets the {@link #CUSTOM} stand-in name and is identified by
     * its {@link ICustomIngredient}. A vanilla ingredient is identified by its backing
     * {@link net.minecraft.core.HolderSet}: a tag-backed set uses the tag's location for the registry name and
     * {@code "tag:<location>"} as the identity; an item-backed set uses the sorted {@code "item:<registry name>"}
     * of every item as the identity -- so two multi-item ingredients are only equal when their whole item set
     * matches, and the kind prefix keeps a tag and an item that share a location distinct -- with the first
     * item's registry name as the representative {@link #registryName}, or {@link #EMPTY} when there are no items
     * at all (e.g. {@code Ingredient.of()}).</p>
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
            Either<TagKey<Item>, List<Holder<Item>>> values = pIngredient.getValues().unwrap();
            this.identity = values.map(
                    tag -> List.of("tag:" + tag.location()),
                    holders -> holders.stream()
                            .flatMap(holder -> holder.unwrapKey().map(key -> "item:" + key.location()).stream())
                            .sorted()
                            .collect(Collectors.toUnmodifiableList())
            );
            this.registryName = values.map(
                    TagKey::location,
                    holders -> holders.stream()
                            .findFirst()
                            .flatMap(holder -> holder.unwrapKey().map(ResourceKey::location))
                            .orElse(EMPTY)
            );
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
        json.add("ingredient", Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, ingredient).getOrThrow());
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
        Ingredient ingredient = Ingredient.CODEC.parse(JsonOps.INSTANCE, pJson.get("ingredient")).getOrThrow();
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
        return ingredient.items().stream()
                .map(item -> new ItemStack(item, count))
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
        // isCustom() must short-circuit first: getValues() throws for a custom ingredient, which is never empty.
        return !ingredient.isCustom() && ingredient.getValues().size() == 0;
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
