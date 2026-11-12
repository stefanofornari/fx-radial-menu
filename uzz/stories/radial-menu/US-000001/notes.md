# Notes for radial-menu/US-000001

## Story Size & Complexity Assessment

**INVEST Check:**
- **Independent:** Yes - no dependencies
- **Negotiable:** Yes - API design can be discussed
- **Valuable:** Yes - core reusable component API
- **Estimable:** Yes - clear scope
- **Small:** Borderline - requires new component class, item model, callback API, extracting from demo
- **Testable:** Yes - clear acceptance criteria

**Sizing:** Estimated 1-2 days. Single domain (radial-menu component). Not too large for single cycle.

**Action:** Proceed to implementation.

---

## Technical Plan

### Target Files
1. **New:** `src/main/java/com/example/radialmenu/RadialMenu.java` - reusable control class
2. **New:** `src/main/java/com/example/radialmenu/MenuItem.java` - item model (label/icon/action)
3. **New:** `src/main/java/com/example/radialmenu/MenuItemSelectionCallback.java` - callback interface
4. **Modify:** `src/main/java/com/example/App.java` - refactor to use new RadialMenu component
5. **New:** `src/test/java/com/example/radialmenu/RadialMenuTest.java` - TDD tests

### Data / State Changes
- `MenuItem` record/class: label (String), icon (optional ImageView/Node), action payload (generic T)
- `RadialMenu` control: ObservableList<MenuItem<?>> items, double innerRadius/outerRadius, selection callback
- Selection state: selectedItem property

### Edge Cases
- Empty items list
- Single item (full circle)
- Many items (small slices)
- Null callback
- Rapid hover/click sequences

---

## TDD Sequence

### Scenario 1: Configure radial menu with item data
1. Red: Test creating RadialMenu with list of MenuItems renders correct segment count
2. Green: Minimal RadialMenu that creates segments from items
3. Triangulate: Test with different item counts, verify geometry

### Scenario 2: Selection callback is emitted
1. Red: Test callback receives selected MenuItem on click
4. Green: Wire click handler to invoke callback
5. Triangulate: Test callback not invoked on hover, only click; test null callback safety

---

## Implementation Log

*2026-11-11*: Started US-000001. Index already shows WIP. Created notes.md.
*2026-11-11*: Completed TDD implementation:
- Created `MenuItem<T>` record with label, optional icon, action payload
- Created `MenuItemSelectionCallback<T>` functional interface
- Created `RadialMenu<T>` extends Pane with configurable items, selection callback
- 7 tests covering: segment creation (3/1/0 items), callback on click, no callback on hover, null callback safety, setCallback after construction
- Refactored `App.java` demo to use new `RadialMenu` component
- All tests pass (mvn test)

### Acceptance Criteria Verification

**Scenario: configure a radial menu with item data**
- ✅ Given a developer provides a list of menu items with labels and actions
- ✅ When the radial menu is created
- ✅ Then the component renders each item as a segment in the circular layout
- Verified by: `createsSegmentsForEachMenuItem`, `emptyItemsListCreatesNoSegments`, `singleItemCreatesOneSegment`

**Scenario: selection callback is emitted**
- ✅ Given a developer has configured a callback on the radial menu
- ✅ When a user clicks an item segment
- ✅ Then the callback receives the selected item and the menu updates its active state
- Verified by: `selectionCallbackReceivesSelectedItemOnClick`, `selectionCallbackNotInvokedOnHover`, `nullCallbackIsSafe`, `callbackCanBeSetAfterConstruction`

### Out-of-Scope Check
- No AtlantaFX integration (deferred)
- No styling hooks/CSS (deferred to US-000002)
- No icon support in demo (MenuItem supports it, demo uses label only)
- No accessibility/i18n (out of scope per scope.md)