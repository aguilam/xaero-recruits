# Xaero Map Recruits

**Short notice:** This mod was "vibe-coded" for personal use and will most likely **not** be actively supported going forward. Use at your own risk.

## Overview

Xaero Map Recruits integrates claims from the **Recruits** mod into **Xaero`s maps** overlays.

## Features

- Show per-chunk claim polygons grouped by faction.
- Display faction name at claim center.
- Configurable label scale, visibility zoom level, and minimap toggle.

## Requirements

- Xaero's Minimap
- Xaero's World Map 
- Villager Recruits 

## Configuration

Open **Options > Mods > Xaero-map-recruits > Config** (or the Forge Mods list) for the in-game config screen.

Client: `config/xaerorecruits-client.toml`

- `claims.showClaims` - show claim highlights.
- `claims.fillOpacity` / `claims.borderOpacity` - fill and border opacity percentages.
- `claims.showTooltips` - claim/faction tooltips.
- `labels.showLabels` - world map labels.
- `labels.showLabelsOnMinimap` - minimap labels. Default: `false` (the minimap stays clean).
- `labels.labelScale` - label size multiplier. The base size is calibrated to Xaero's waypoint names minus about two pixels.
- `labels.minimapLabelMaxZoom` - only used if minimap labels are enabled; hides them at farther-out minimap zoom levels. Default: `4`.
- `labels.worldMapLabelMinScale` - labels disappear below this map scale. Default: `0.25`.
- `labels.worldMapLabelMaxScale` - labels disappear above this map scale. Default: `4.0`, so labels are visible only in the 0.25x-4x band.
- `mapClaiming.enabled` - show claim/expand actions in Xaero's world map and minimap menus. Default: `true`.
This is a client-side display option only: turning it off just hides the buttons. It does not enable or disable claiming, and there is no matching server setting.

On first launch, the label size from the original mod's `config/journeyrecruits-client.toml` is imported once. The old file is not modified. `labelFontScale` becomes `labelScale`. Old zoom cutoffs are intentionally not imported: the current renderer uses the 0.25x-4x world-map visibility band and keeps minimap labels off by default. The import is marked by `meta.originalConfigImported`; delete the new config (and the old file if you want a truly clean start) to import again.

For claiming to work, the server must also have `AllowClaiming = true` in the world's `serverconfig/recruits-server.toml` (the default is `true`) and enough currency in the player's inventory. Claiming is always possible through the map when those normal Recruits rules allow it; if they do not, the map buttons are simply disabled.

## Configuration

- If the build uses **Cloth Config + AutoConfig**, open the Mod Options / Config screen (or Mod Menu) to edit:
  - `nameAppearMaxZoom` — maximum map zoom at which faction labels appear.
  - `fontScale` — multiplier for faction label size.
  - `showOnMinimap` — toggle showing labels on minimap (may not be supported depending on JourneyMap version).
- If using the plain **ForgeConfigSpec** variant, edit the generated TOML files in the `config/` folder.

## Map claiming

When the client option is enabled:

- On Xaero's World Map, right-click an unclaimed chunk.
- On Xaero's Minimap, right-click the map. The chunk under the cursor is used; keep the click near the player if the distance limit blocks the target.

The menu offers:

- **Claim 5x5 area** - creates a new 5x5 claim centered on the clicked chunk.
- **Expand claim by one chunk** - appears when the clicked chunk is next to one of your faction's claims.

The client prepares only a small action request. The server re-checks the player's faction permissions, distance (4 chunks), buffer zones, claim size, overlap, dimension and payment before changing anything. Recruits 1.15.0's own `MessageUpdateClaim` accepts a full claim from the client without these checks, so this mod uses its own validated packet instead.

## Building from source

From the project root (Gradle wrapper included):

```bash
# Run the client (dev environment)
./gradlew runClient

# Build the release jar
./gradlew build
# Built jar will be in build/libs/
```