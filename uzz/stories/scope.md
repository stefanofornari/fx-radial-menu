# Project Name: continuous-radial-menu
**Version:** 0.0.0-SNAPSHOT

## Overall Description

This project delivers a reusable JavaFX radial menu component and a simple demo application that shows the control in use. The component arranges menu items as wedge-shaped segments around a central hub and reports selections through a callback. Wedge labels are an existing, delivered behaviour of the component.

This scope covers FR-2 from the feature-request library: making the control's overall footprint a bindable, runtime-adjustable `diameter`, with the hub proportion exposed as `innerRadiusRatio`, so a host application can bind the menu to another control's size (for example, the current prompt input height) and keep the menu circular and layout-stable. The deliverable is the sizing API, geometry that rescales around the centre of its own square, clipped rendering with a uniform stroke, defined behaviour for invalid values, and a demo that exercises runtime resizing. Label rendering rules stay as they are; only their behaviour under a diameter change is in scope.

## In-Scope Capabilities

* **radial-menu**:
  - [ ] Add a public styleable `DoubleProperty diameter` representing the overall circular footprint (outer diameter), with `diameterProperty()`, `diameter()`, and `diameter(double)` accessors.
  - [ ] Default `diameter` to `270`, preserving the current nominal footprint.
  - [ ] Make `diameter` fully bindable, e.g. `radialMenu.diameterProperty().bind(prompt.heightProperty())`, and react to runtime changes from a binding as well as a direct set.
  - [ ] Add a public `DoubleProperty innerRadiusRatio` controlling the hub opening relative to the outer radius, with `innerRadiusRatioProperty()`, `innerRadiusRatio()`, and `innerRadiusRatio(double)` accessors.
  - [ ] Default `innerRadiusRatio` to `60 / 135` (approximately 0.4444), preserving the current hub proportion.
  - [ ] Clamp `innerRadiusRatio` on write into `[0, 1)` so the property never exposes an out-of-range value, including when set through a binding.
  - [ ] Keep both existing constructors unchanged and add no required constructor parameters.
  - [ ] Derive geometry from the properties: outer radius = `diameter / 2`, inner radius = outer radius × `innerRadiusRatio`, with the circle centred at `(diameter / 2, diameter / 2)` in the control's local coordinates.
  - [ ] Rebuild or rescale all geometry whenever `diameter` or `innerRadiusRatio` changes so wedges, hub opening, and labels remain circular and scale proportionally.
  - [ ] Set `prefWidth`, `prefHeight`, `minWidth`, `minHeight`, `maxWidth`, and `maxHeight` to the current diameter so the control has a fixed, square footprint.
  - [ ] Report layout bounds exactly `[0, 0, diameter, diameter]`, with no negative-coordinate geometry and no child painting that extends the footprint.
  - [ ] Clip rendered content to the `[0, 0, diameter, diameter]` square so the outer stroke cannot enlarge or shift the layout bounds.
  - [ ] Use a uniform 2 px stroke for normal, hover, and selected states; hover and selection remain distinguishable through their fill and border colours rather than stroke width.
  - [ ] Keep layout bounds unchanged across hover and selection transitions.
  - [ ] Render no wedges for a non-positive diameter, and make the measured width and height track the (non-negative) diameter.
  - [ ] Preserve the selected item, its highlight, hover behaviour, selection callback, and item-to-wedge mapping across diameter and ratio changes and across geometry rebuilds.
  - [ ] Reposition labels using the existing `labelRadiusRatio` contract relative to the new radii, without rotating them and without letting them intercept mouse events.
  - [ ] Keep an explicit `-rm-label-font-size` absolute: labels are not transformed or scaled by diameter changes; labels that no longer fit are truncated with an ellipsis inside their wedge.
  - [ ] Keep the unset label font-size default derived from the diameter as before, so default labels continue to track the control size.
  - [ ] Add the `radial-menu-label` class, `-rm-label-fill`, and icon-follows-label behaviour to the resized paths, keeping all existing label styling hooks working at every diameter.
  - [ ] Ensure clicking or hovering any label still selects or hovers its wedge after a resize.

* **demo**:
  - [ ] Add a diameter slider to the demo that drives the menu's `diameter` property at runtime.
  - [ ] Display the current diameter value next to the slider.
  - [ ] Include at least one binding example showing the menu sized from another control's dimension.
  - [ ] Use the demo to validate clipping, centring, and label truncation at small and large diameters.

## Out-of-Scope / Future Enhancements

* Changes to the label rendering contract itself: arrangement, icon-above-label layout, the shrink-then-truncate fit algorithm, label collision avoidance, and label styling hooks beyond keeping them correct under resize.
* Rotated, curved, vertical, or wrapped labels; tooltips for truncated labels; label animations.
* Independent width and height, elliptical or non-circular layouts, per-segment radii, and arbitrary stroke widths.
* Central hub content, hub text, central selection button, or hub interaction.
* Animated transitions during diameter changes, and DPI- or `snapToPixel`-specific sizing behaviour.
* Automatic geometry rebuild when `items()` is mutated beyond the existing behaviour.
* Accessibility features, keyboard navigation, and internationalization.
* Library packaging or distribution workflows beyond the component and demo.
* Non-desktop platforms and gesture-based interaction models.
* Additional label theming such as per-state label colours or per-item label styling.

## Technical Context & Infrastructure

* **Development Toolchain**: Java 25 + JavaFX + Maven
* **Target Systems/Platforms**: JavaFX desktop applications
* **Project Reference Docs**:
  - Coding Standard: `uzz/project/coding_standard.md`
  - Development Framework: `uzz/project/development_framework.md`
  - Feature Requests: `uzz/library/fx-radial-menu-feature-requests.md`

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
- sizing
- diameter
- inner-radius-ratio
- layout-bounds
- clipping
- fr-2
- css
