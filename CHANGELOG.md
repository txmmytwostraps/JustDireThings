## [1.0.0]

Unofficial Forge 1.18.2 port of Just Dire Things. Based on An0mz's 1.20.1 backport and Direwolf20's 1.21.1 version 1.5.7, plus his later changes on the 1.21.1 branch up to August 8, 2026.

### Added

- Dire Smithing Table. The 1.18.2 smithing table only has two slots, so tier upgrades, ability upgrades and Paxels are made here.
- The Dire Smithing Table also does regular smithing when the template slot is empty.
- Hoppers and pipes can fill it from any side.
- JEI page and guidebook entry for it.
- Dimension restrictions for the Vault Hunters vault dimensions. Each feature has its own switch in `restricted_dimensions` in the server config.
- On by default: Block Breakers, Placers, Swappers, Clickers, the Paradox Machine, Portal Guns, the Time Wand, Phase and the Creature Catcher.
- Off by default: Fluid Placers, Void Shift and Eclipse Gate.
- `use_sculk_recipes` in the common config. Turn it on to use Wild Backport's sculk items. Off by default.

### Changed

- Config values always apply. This covers tool break limits, the minimum machine speed, the Player Accessor check time and the Advanced Polymorphic Wand's energy cost.

### Fixed (compared to 1.21.1)

- The Item Collector follows its rotation.
- Number keys and drags on filter slots no longer lose items.
- Shift-clicking glass bottles into a Potion Canister no longer deletes them.
- The Advanced Portal Gun no longer fires at a favorite in a missing dimension.

### Notes

- Config changes need a restart.
- Set `use_sculk_recipes` the same on the client and the server. Forge does not sync it.
- Needs Forge 40.3.6 or newer. JEI, Patchouli, Curios and Wild Backport are optional.
- Everything from 1.21.1 is here except armor trims. 1.18.2 doesn't have them.
