# AlchemyLib — Migration Notes (1.20.1 → 1.21.1)

This release ports AlchemyLib from Forge 1.20.1 to NeoForge 21.1.228 / Minecraft 1.21.1. The
update is **API-breaking**. The notes below cover every change a downstream mod needs to make.

The mod id (`alchemylib`) and Java package roots (`com.smashingmods.alchemylib.*`) are unchanged.

---

## Recipe API

### Constructor

`AbstractProcessingRecipe` no longer carries the recipe id internally — 1.21 routes ids through
`RecipeHolder` instead of the recipe instance.

```java
// before (1.20.1)
public class MyRecipe extends AbstractProcessingRecipe {
    public MyRecipe(ResourceLocation pId, String pGroup, ...) {
        super(pId, pGroup);
    }
}

// after (1.21.1)
public class MyRecipe extends AbstractProcessingRecipe {
    public MyRecipe(String pGroup, ...) {
        super(pGroup);
    }
}
```

`getId()` is still available, but the id is now set after construction via a new
`setId(ResourceLocation)` mutator — typically called by the recipe registry helper when
resolving a recipe by its `RecipeHolder.id()`.

### `matches` / `assemble` / `getResultItem`

`Recipe<Inventory>` is now `Recipe<RecipeInput>`, and `RegistryAccess` is now
`HolderLookup.Provider`:

```java
// before
public boolean matches(Inventory pContainer, Level pLevel) { ... }
public ItemStack assemble(Inventory pContainer, RegistryAccess pRegistryAccess) { ... }
public ItemStack getResultItem(RegistryAccess pRegistryAccess) { ... }

// after
public boolean matches(RecipeInput pInput, Level pLevel) { ... }
public ItemStack assemble(RecipeInput pInput, HolderLookup.Provider pProvider) { ... }
public ItemStack getResultItem(HolderLookup.Provider pProvider) { ... }
```

`RecipeInput.getItem(int)` replaces `Inventory.getItem(int)` for slot access inside these methods.

---

## IngredientStack

The hand-rolled JSON and packet helpers are removed. Use the codecs:

| removed                                | replacement                       |
|----------------------------------------|-----------------------------------|
| `IngredientStack.toJson()`             | `IngredientStack.CODEC`           |
| `IngredientStack.fromJson(JsonObject)` | `IngredientStack.CODEC`           |
| `IngredientStack.toNetwork(buf)`       | `IngredientStack.STREAM_CODEC`    |
| `IngredientStack.fromNetwork(buf)`     | `IngredientStack.STREAM_CODEC`    |

`getRegistryName()` semantics are unchanged from 1.20.1 — the first value's tag id for
tag-based ingredients, the first value's item id for item-based ingredients.

---

## DatagenHelpers

The codec rewrite makes the pre-codec JSON-builder helpers obsolete. The following methods are
removed; build through `Codec`/`StreamCodec` instead:

- `itemToJson`
- `itemStackToJson`
- `itemStackListToJson`
- `ingredientStackListToJson`
- `fluidStacktoJson`
- `andCondition`
- `orCondition`
- `tagEmptyCondition`
- `tagNotEmptyCondition`

The string → `ItemStack`/`IngredientStack` helpers and the `ResourceLocation` formatters are kept.

---

## Capabilities

NeoForge 1.21 moved capability registration from the block entity to the consuming mod. The
block-entity helpers in AlchemyLib no longer override `getCapability(...)` — that hook isn't
called by vanilla. Consumers register capabilities themselves on `RegisterCapabilitiesEvent`,
using the existing per-side accessors on the block entity.

```java
@EventBusSubscriber(modid = MyMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class Capabilities {

    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
                MyBlockEntities.MY_MACHINE.get(),
                (be, side) -> be.getItemHandler(side));

        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
                MyBlockEntities.MY_MACHINE.get(),
                (be, side) -> be.getFluidHandler(side));

        event.registerBlockEntity(
                net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK,
                MyBlockEntities.MY_MACHINE.get(),
                (be, side) -> be.getEnergyHandler(side));
    }
}
```

`getItemHandler(Direction)`, `getFluidHandler(Direction)`, and `getEnergyHandler(Direction)` are
provided by the AlchemyLib block-entity base classes — call sides may be null where the block
entity exposes a single shared handler.

---

## Networking

NeoForge replaced Forge's `SimpleChannel` with the payload-handler system. `AbstractPacketHandler`
now wraps `PacketDistributor` and is wired through `RegisterPayloadHandlersEvent`:

```java
public final class MyMod {
    public static final PacketHandler PACKET_HANDLER = new PacketHandler();

    public MyMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::registerPayloads);
        // ...
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PACKET_HANDLER.register(event.registrar(MODID));
    }
}
```

Each packet becomes a `record` implementing `AlchemyPacket`, with a `Type<P>`, a `StreamCodec`,
and a `void handle(IPayloadContext context)` method. See AlchemyLib's own `BlockEntityPacket` and
`ToggleLockButtonPacket` for reference implementations.

The send helpers on `AbstractPacketHandler` (`sendToServer`, `sendToPlayer`, `sendToAll`,
`sendToNear`, `sendToTrackingChunk`) keep the same signatures. `sendToNear` and
`sendToTrackingChunk` now fail loudly via `Preconditions.checkState` if called with a client
`Level` — server-only methods are documented as such.

For full platform-level details on the payload-handler system, refer to NeoForge's own migration
guide.
