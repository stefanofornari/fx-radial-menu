# Coding Standard

## Purpose

This coding standard applies to the JavaFX UI code in `src/main/java` and to any reuse of the radial-menu component within desktop JavaFX applications.

## General Principles

- Prefer clear, readable, and maintainable code.
- Keep components small and focused.
- Favour composition over duplication.
- Write code that is easy to test.
- Keep geometry, state transitions, and styling logic explicit.

## Java Conventions

- Use package names aligned with the project namespace (`com.example`).
- Prefer descriptive names for geometry values such as `innerRadius`, `outerRadius`, and `startAngle`.
- Keep UI helper classes small and scoped to a single visual concern, as shown by the nested `RingSegmentWedge` implementation.
- Use `final` fields for values that are not meant to change after initialization.

## Testing Conventions

- [TBD] No automated test framework is currently configured.
- [TBD] Add targeted JavaFX tests when the component is formalized for reuse.

## Documentation Conventions

- Document the purpose and geometry of each menu element.
- Explain hover and pressed interactions when they affect component behavior.
- Keep the project documentation aligned with the Java 25 and JavaFX 25 configuration.

### Keywords
- coding-standard
- conventions
- style
- quality
- testing
- javafx
- java25
