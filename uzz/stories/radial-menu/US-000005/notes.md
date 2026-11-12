# Notes for radial-menu/US-000005

## Story Size & Complexity Assessment
- **INVEST:** Small; adds component-specific CSS customization variables with fallback chain. No Java code changes needed.
- **Action:** Proceed.

## Technical Plan
### Target Files
- `src/main/resources/com/example/radialmenu/radial-menu.css` — add `-radial-menu-segment-*` looked-up color variables with three-tier fallback: custom → AtlantaFX button colors → built-in grays

### Data / State Changes
- None; purely CSS variable definitions.

### Edge Cases
- Fallback chain ensures graceful degradation: custom vars → AtlantaFX theme vars → hardcoded defaults.
- Variables can be overridden at Scene root (`.root`) or component level (`.radial-menu-segment`) via CSS specificity.

## Implementation Log
- 2026-11-12: Marked US-000005 WIP in index.
- 2026-11-12: Updated radial-menu.css with 6 customization variables using `-rm-` prefix:
  - `-rm-segment-bg` (default fill)
  - `-rm-segment-border` (default stroke)
  - `-rm-segment-bg-hover` (hover fill)
  - `-rm-segment-border-hover` (hover stroke)
  - `-rm-segment-bg-selected` (selected fill)
  - `-rm-segment-border-selected` (selected stroke)
- 2026-11-12: Refactored to define defaults in `.root` with three-tier fallback (custom → AtlantaFX button vars → hardcoded grays). Style rules reference `-rm-*` variables only. All tests pass.

## Decisions / Trade-offs
- Used component-prefixed variable names (`-rm-segment-*`) to avoid collisions with host application CSS.
- Three-tier fallback defined once in `.root`: 1) custom override, 2) AtlantaFX button vars, 3) original grays.
- Style rules are clean - only reference `-rm-*` variables.
- No Java API changes; purely CSS-based customization.

### Keywords
- notes
- us-000005
- css
- customization
- looked-up-colors
- fallback