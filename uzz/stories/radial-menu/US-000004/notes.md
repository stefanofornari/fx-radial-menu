# Notes for radial-menu/US-000004

## Story Size & Complexity Assessment
- **INVEST:** Small; adds AtlantaFX dependency and updates CSS to use looked-up color variables. Single domain (radial-menu styling).
- **Action:** Proceed.

## Technical Plan
### Target Files
- `pom.xml` — add AtlantaFX base dependency (2.1.0, compatible with Java 21)
- `src/main/resources/com/example/radialmenu/radial-menu.css` — replace hardcoded colors with AtlantaFX looked-up color variables (`-color-button-bg`, `-color-button-border`, `-color-button-bg-hover`, `-color-button-border-hover`, `-color-button-bg-pressed`, `-color-button-border-pressed`)
- `src/main/java/com/example/App.java` — apply AtlantaFX theme (PrimerLight) as user agent stylesheet, update inline styles to use color variables

### Data / State Changes
- No runtime state changes; purely styling integration.
- Component now adapts to any AtlantaFX theme (light/dark variants).

### Edge Cases
- AtlantaFX 3.x requires Java 25; used 2.1.0 for Java 21 compatibility.
- Theme colors resolve via CSS looked-up color mechanism at Scene root.

## Implementation Log
- 2026-11-12: Created US-000004, marked WIP in index.
- 2026-11-12: Added `io.github.mkpaz:atlantafx-base:2.1.0` to pom.xml.
- 2026-11-12: Updated radial-menu.css to use AtlantaFX button color variables for base/hover/selected states.
- 2026-11-12: Updated App.java demo to set `PrimerLight` user agent stylesheet and use `-color-bg-default`, `-color-fg-default`, `-color-border-default` variables.
- 2026-11-12: `mvn compile` and `mvn test` pass.

## Decisions / Trade-offs
- Used AtlantaFX 2.1.0 (Java 21 compatible) instead of 3.x (Java 25+).
- Mapped radial menu states to button color variables: default→button-bg, hover→button-bg-hover, selected→button-bg-pressed. This aligns visual behavior with AtlantaFX buttons.
- Demo uses PrimerLight theme; dark theme (PrimerDark, Dracula, CupertinoDark, etc.) would work automatically by changing the stylesheet.

### Keywords
- notes
- us-000004
- atlantafx
- theming
- css
- looked-up-colors