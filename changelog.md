# AlchemyLib 1.21.5-1.1.9 RELEASE

Shared library for Alchemistry and other ChemLib add-ons.

Changes:
- Updated to Minecraft 1.21.5 / NeoForge. First AlchemyLib release for 1.21.5.
- Fixed a crash when recipes use custom ingredients from other mods.
- Recipe ingredients are now identified by the whole ingredient rather than just their first item, so similar ingredients no longer get mixed up.
- Machines now remember the recipe you selected instead of forgetting it.
- Ported machine-screen rendering to Minecraft 1.21.5's new rendering pipeline.
- Fixed machine screens drawing their background darker than intended.
