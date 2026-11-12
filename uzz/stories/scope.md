# Project Name: continuous-radial-menu
**Version:** 1.0-SNAPSHOT

## Overall Description

This project delivers a reusable JavaFX radial menu component and a simple demo application that shows the control in use. The component arranges menu items as wedge-shaped segments around a central hub, while the demo exists to validate behavior and provide a reference integration example for desktop JavaFX applications.

## In-Scope Capabilities

* **radial-menu**:
  - [ ] Provide a reusable JavaFX control that renders menu entries as wedge-shaped segments around a central hub.
  - [ ] Expose a developer-facing API for menu items, labels or icons, size, and selection callback handling.
  - [ ] Support hover visual feedback and selection by click.
  - [ ] Use AtlantaFX theme tokens and the default AtlantaFX skin as the default visual style for the component.
  - [ ] Allow look-and-feel customization through documented JavaFX styling hooks.

* **demo**:
  - [ ] Include a simple JavaFX demo application that shows the radial menu in context.
  - [ ] Use the demo to validate layout, interaction, and styling behavior during development.

## Out-of-Scope / Future Enhancements

* Advanced animations, complex gesture models, or non-desktop platforms.
* A formal library packaging or distribution workflow beyond the project demo and component implementation.
* Accessibility features, keyboard-only navigation, or internationalization beyond the base component contract.

## Technical Context & Infrastructure

* **Development Toolchain**: Java 25 + JavaFX + Maven
* **Target Systems/Platforms**: JavaFX desktop applications
* **Project Reference Docs**:
  - Coding Standard: `uzz/project/coding_standard.md`
  - Development Framework: `uzz/project/development_framework.md`

### Keywords
- scope
- requirements
- boundaries
- in-scope
- out-of-scope
- javafx
- radial-menu
- component
- demo
