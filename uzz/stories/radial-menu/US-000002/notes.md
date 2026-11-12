# Notes for radial-menu/US-000002

## Story Size & Complexity Assessment
- **INVEST:** Small, single-domain UI interaction change in `RadialMenu`; no schema/API contract break beyond additive selection state access.
- **Action:** Proceed (no breakdown needed).

## Technical Plan
### Target Files
- `src/main/java/com/example/radialmenu/RadialMenu.java` — add hover/selected state handling and selected-item tracking.
- `src/test/java/com/example/radialmenu/RadialMenuTest.java` — TDD coverage for hover state, click selection state, and single callback invocation.
- `uzz/stories/index.md` — status tracking (`TODO`→`WIP`→`DONE`).

### Data / State Changes
- Track active `selectedItem`.
- Expose `getSelectedItem()` for consumers/demo integration.
- Use documented JavaFX style hooks via style classes on segments: base `radial-menu-segment`, transient `hovered`, active `selected`.

### Edge Cases
- Hover enter/exit toggles `hovered` without invoking callback.
- Clicking one segment clears `selected` from others.
- Callback fires once per click (existing callback contract preserved).
- Null callback remains safe.

## Implementation Log
- 2026-11-11: Marked US-000002 `WIP` in index, gathered context (US-000001 done, existing `RadialMenu`/tests).
- 2026-11-11: Added failing tests for hover style class, hover removal, selected item/style on click, and callback invoked once.
- 2026-11-11: Implemented minimal segment interaction handlers and selection state in `RadialMenu`; all tests passing.

## Decisions / Trade-offs
- Chose style-class toggling (`hovered`, `selected`) over hard-coded colors to keep look-and-feel customizable through JavaFX CSS/styling hooks as required.
- Selection state is stored in control (`selectedItem`) rather than only in node styles to support programmatic/demo inspection.

### Keywords
- notes
- us-000002
- hover
- selection
- tdd
