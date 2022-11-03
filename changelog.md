# AlchemyLib 1.19.2-1.0.18 RELEASE

Everything seems to work, so I'm doing a full release build.

Changes:
- Add heat capability.
- Split searching functionality from processing block entity into an abstract class which can be optionally extended.
- Remove slot handler from fluid block entity. Make AbstractFluidBlockEntity also implement InventoryBlockEntity. It's fine if either input or output isn't used and is up to the implementer to decide.
  Remove Nameable from AbstractProcessingBlockEntity.

Fixes:
- Remove redundant AutomationSlotHandler.
- Add default dropContents to InventoryBlockEntity to dedupe code.
- Add missing localization for "Show Recipes". Wasn't localized before.
- Fixed an issue with the FakeItemRenderer not working with Element and Chemical items. They had no transparency.