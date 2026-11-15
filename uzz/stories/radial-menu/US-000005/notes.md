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
- 2026-11-15: Added `-rm-label-fill-hover` and `-rm-label-fill-selected`; label groups now receive the wedge's hover and selected pseudo-classes. Added acceptance coverage for state-colour customization.
- 2026-11-15: Moved built-in grayscale defaults to a low-specificity `RadialMenu` type selector so `.radial-menu` rules from Scene/application stylesheets win. Discovered that looked-up colour fallback chains do not resolve when the first variable is undefined, and that AtlantaFX `-color-button-*` tokens are scoped to `.button`, not `.root`; the integration theme now maps to root-level `-color-*` tokens.

## Decisions / Trade-offs
- Used component-prefixed variable names (`-rm-*`) to avoid collisions with host application CSS.
- Built-in grayscale defaults live on the `RadialMenu` type selector (specificity 0,0,1) so any `.radial-menu` (0,1,0) rule in an author stylesheet overrides them without needing a `!important` or a stylesheet attached to the component instance.
- The optional default theme and the AtlantaFX integration theme both target `.radial-menu` and are loaded as Scene stylesheets.
- AtlantaFX mappings use root-level palette tokens (`-color-bg-subtle`, `-color-border-default`, `-color-base-1`, `-color-accent-emphasis`, `-color-fg-default`, `-color-fg-emphasis`) because `.radial-menu` is a `Pane` and does not inherit `.button`-scoped variables.
- Selected state uses the accent emphasis colour so it remains clearly distinguishable from the default and hover states.
- No Java API changes; customisation remains CSS-based, with a small amount of Java code to propagate hover/selected state to labels.

### Keywords
- notes
- us-000005
- css
- customization
- looked-up-colors
- fallback