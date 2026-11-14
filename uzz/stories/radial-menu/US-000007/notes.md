# Notes for radial-menu/US-000007

## Story Size & Complexity Assessment
- **INVEST:** Borderline but acceptable. One component plus CSS, demo, README, and tests. No unrelated domains.
- **Action:** Proceed without splitting.

## Technical Plan

### Target Files
- `src/main/java/ste/fx/control/radialmenu/RadialMenu.java` — label layer, properties, fit algorithm, CSS metadata.
- `src/test/java/ste/fx/control/radialmenu/RadialMenuLabelsTest.java` — story acceptance tests.
- `src/main/resources/ste/fx/control/radialmenu/radial-menu.css` — label fill variable and label rule.
- `src/main/resources/ste/fx/control/radialmenu/radial-menu-atlantafx.css` — label fill mapped to AtlantaFX text colour.
- `src/main/resources/ste/fx/control/radialmenu/radial-menu-theme.css` — label fill fallback.
- `src/main/java/ste/fx/control/App.java` — demo icon and label visibility.
- `README.md` — label API and CSS hooks.

### Data / State Changes
- New `labelRadiusRatio` styleable `DoubleProperty` (`-rm-label-radius-ratio`, default 0.5, clamped `[0,1]`).
- New `labelFontSize` styleable `DoubleProperty` (`-rm-label-font-size`, default 270 × 0.05 = 13.5).
- New `labels()` accessor returning the rendered label `Text` nodes in item order.
- `items()` gains a list-change listener that rebuilds wedges and labels.

### Edge Cases
- Empty or blank labels render no text.
- Empty item list renders no segments and no labels.
- Long labels shrink to 70% of the base size, then truncate with an ellipsis.
- An explicit CSS font size disables shrinking; truncation only.
- Icon-only fallback when icon and label do not fit together.
- Ratio clamped at both bounds; negative/above-one values never reach geometry.
- Labels layer is mouse-transparent so pick events reach wedges.

## Implementation Log
- 2026-11-14: Marked US-000007 WIP in index; planned label layer and styleable properties.
- 2026-11-14: Implemented `labels()`, `labelRadiusRatio`, `labelFontSize` with CSS metadata; label layer and per-wedge groups; fit/truncate and icon logic.
- 2026-11-14: Added `-rm-label-fill` to the component and theme stylesheets, updated demo with an icon, documented labels in README.
- 2026-11-14: Added `openjfx-monocle` as a test dependency so CSS-driven scenarios can run headless.
- 2026-11-14: 31 tests green under `mvn clean package`; marked US-000007 DONE.
- 2026-11-14: Reduced the component hub radius from 75 to 60 (20%) so wedges reach the centre circle; label anchors follow the new band.

## Decisions / Trade-offs
- Labels live in a single mouse-transparent `Group` child of the menu; per-wedge `Group`s centre content on the mid-angle anchor.
- Font size and radius ratio are implemented as CSS metadata properties (looked-up colours cannot carry sizes).
- Fit uses the chord width at the label radius and the radial band depth as the usable box, with a safety margin.
- Explicit font size is detected by writes through the setter or through CSS application; the untouched default remains shrinkable.
- `-rm-label-fill` has no default in the component stylesheet, so an author rule on `.radial-menu` wins; without a theme the text keeps the JavaFX default fill and CSS logs an unresolved-variable warning.
- Icon and label share a wedge only when the icon fits the combined icon area at natural size; otherwise the label is dropped and the icon scales alone in the full label box.

### Keywords
- notes
- us-000007
- labels
- javafx
- css
- fitting
