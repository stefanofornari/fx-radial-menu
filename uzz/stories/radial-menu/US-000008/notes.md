# Notes for radial-menu/US-000008

## Story Size & Complexity Assessment
- **INVEST:** Within range. One component plus demo, README, and tests; no unrelated domains.
- **Action:** Proceed without splitting.

## Technical Plan

### Target Files
- `src/main/java/ste/fx/control/radialmenu/RadialMenu.java` — sizing properties, centred geometry, clip, derived label size.
- `src/test/java/ste/fx/control/radialmenu/RadialMenuSizingTest.java` — sizing acceptance tests.
- `src/main/java/ste/fx/control/App.java` — diameter slider, readout, centre binding.
- `src/main/resources/ste/fx/control/radialmenu/radial-menu.css` — uniform 2 px selected stroke.
- `README.md` — sizing API and uniform-stroke documentation.

### Data / State Changes
- New `diameter` and `innerRadiusRatio` double properties with public accessors.
- Geometry derives from the properties; sizes and clip track the effective diameter.
- Default label font size re-derives from the diameter unless explicitly set or bound.

### Edge Cases
- Non-positive diameters render nothing; effective sizes are clamped to zero.
- `innerRadiusRatio` clamps to `[0, 1)` on write and on read when bound.
- Selected item, hover, callback, and label state survive rebuilds.
- Explicit CSS label font sizes stay fixed across resizes.

## Implementation Log
- 2026-11-14: Marked US-000008 WIP in index.
- 2026-11-14: Added properties, centred geometry around `(diameter/2, diameter/2)`, square clip, size constraints, derived label font, uniform 2 px selected stroke.
- 2026-11-14: Demo binds diameter to a slider with a live readout; menu and hub stay centred.
- 2026-11-14: 47 tests green under `mvn clean package`; marked US-000008 DONE.

## Decisions / Trade-offs
- `diameter` stores non-negative values through its setter; bound negative values are clamped at use so the control never sizes below zero.
- `innerRadiusRatio` clamps in `set` and also in `get`, so a bound out-of-range value still reports a valid ratio to callers.
- The clip is a square rather than a circle: it keeps the layout bounds contract, and outer rim strokes are shaved rather than expanding the footprint.
- Derived label font updates skip explicitly set and bound font sizes.

### Keywords
- notes
- us-000008
- sizing
- diameter
- clipping
- javafx
