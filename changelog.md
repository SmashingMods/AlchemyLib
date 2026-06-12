# AlchemyLib 1.21.3-1.1.7 RELEASE

Shared library for Alchemistry and other ChemLib add-ons.

Changes:
- Updated to Minecraft 1.21.3 / NeoForge. First AlchemyLib release for 1.21.3.
- Fixed a crash at world creation when a recipe uses a NeoForge custom or compound ingredient.
- Fixed recipes silently merging tag-backed, custom, or multi-item ingredient inputs that should stay distinct.
- Added persistent recipe selection: processing machines can remember the recipe the player chose in the recipe selector instead of reverting to the first matching recipe.
- Fixed machine screens rendering with a much darker background than vanilla container screens.
