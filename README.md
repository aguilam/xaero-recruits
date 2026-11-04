# Xaero Map Recruits

**Short notice:** This mod was "vibe-coded" for personal use and will most likely **not** be actively supported going forward. Use at your own risk.

## Overview

Xaero Map Recruits integrates claims from the **Recruits** mod into **Xaero`s maps** overlays.

## Features

- Show per-chunk claim polygons grouped by faction.
- Display faction name at claim center.
- Configurable label scale, visibility zoom level, and minimap toggle.
- Claim chunks from Xaero's map

## Requirements

- Xaero's Minimap
- Xaero's World Map 
- Villager Recruits

## Configuration

- `showClaims` - toggle showing faction claims and labels on maps.
- `showOnMinimap` - toggle showing faction labels on minimap.
- `worldMapLabelScale` / `minimapLabelScale` - multiplier for faction label size on maps.
- `minimapLabelMaxZoom` - maximum minimap zoom at which faction labels appear.
- `worldMapLabelMinScale` / `worldMapLabelMaxScale` - minimum and maximum world map scales at which faction labels appear.

## Building from source

From the project root (Gradle wrapper included):

```bash
# Run the client (dev environment)
./gradlew runClient

# Build the release jar
./gradlew build
# Built jar will be in build/libs/
```