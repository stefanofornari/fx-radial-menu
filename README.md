# fx-radial-menu

A reusable JavaFX radial menu component with AtlantaFX theming support.

## Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>com.github.stefanofornari</groupId>
    <artifactId>fx-radial-menu</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### Basic Usage

```java
import ste.fx.control.radialmenu.MenuItem;
import ste.fx.control.radialmenu.RadialMenu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;

public class MyApp extends Application {
    @Override
    public void start(Stage stage) {
        var items = List.of(
            new MenuItem<>("Action A", "actionA"),
            new MenuItem<>("Action B", "actionB"),
            new MenuItem<>("Action C", "actionC")
        );

        RadialMenu<String> menu = new RadialMenu<>(items, selected -> {
            System.out.println("Selected: " + selected.label());
        });

        Pane root = new Pane(menu);
        menu.layoutXProperty().bind(root.widthProperty().divide(2));
        menu.layoutYProperty().bind(root.heightProperty().divide(2));

        stage.setScene(new Scene(root, 400, 400));
        stage.show();
    }
}
```

## API Reference

### `RadialMenu<T>`

Extends `Pane`. Renders menu items as wedge-shaped segments around a central hub.

#### Constructors

| Constructor | Description |
|-------------|-------------|
| `RadialMenu(List<MenuItem<T>> items)` | Creates menu with items, no selection callback |
| `RadialMenu(List<MenuItem<T>> items, MenuItemSelectionCallback<T> callback)` | Creates menu with items and selection callback |

#### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `setSelectionCallback(MenuItemSelectionCallback<T> callback)` | `void` | Sets or updates the selection callback |
| `getSelectedItem()` | `MenuItem<T>` | Returns the currently selected item (null if none) |
| `getItems()` | `ObservableList<MenuItem<T>>` | Returns the list of menu items |
| `getSegments()` | `ObservableList<Node>` | Returns the visual segment nodes |

### `MenuItem<T>`

Record representing a menu entry.

| Field | Type | Description |
|-------|------|-------------|
| `label` | `String` | Display text for the segment |
| `icon` | `Node` | Optional icon (can be null) |
| `actionPayload` | `T` | Generic payload returned on selection |

#### Factory Methods

```java
// Label + payload only
new MenuItem<>("Label", "payload");

// Label + icon + payload
new MenuItem<>("Label", iconNode, "payload");
```

### `MenuItemSelectionCallback<T>`

Functional interface for selection notifications.

```java
@FunctionalInterface
public interface MenuItemSelectionCallback<T> {
    void onSelection(MenuItem<T> selectedItem);
}
```

## CSS Customization Variables

The component uses **looked-up colors** for theming. Define these on the `.radial-menu` style class (or any ancestor) to customize appearance.

| Variable | Fallback Chain | Default | Applies To |
|----------|----------------|---------|------------|
| `-rm-segment-bg` | → `-color-button-bg` → `#E0E0E0` | Light gray | Segment fill (default) |
| `-rm-segment-border` | → `-color-button-border` → `#CCCCCC` | Medium gray | Segment stroke (default) |
| `-rm-segment-bg-hover` | → `-color-button-bg-hover` → `#CCCCCC` | Medium gray | Segment fill (hover) |
| `-rm-segment-border-hover` | → `-color-button-border-hover` → `#AAAAAA` | Darker gray | Segment stroke (hover) |
| `-rm-segment-bg-selected` | → `-color-button-bg-pressed` → `#999999` | Dark gray | Segment fill (selected) |
| `-rm-segment-border-selected` | → `-color-button-border-pressed` → `#888888` | Darker gray | Segment stroke (selected) |

### Example: Custom Theme

```css
.radial-menu {
    -rm-segment-bg: #2D2D2D;
    -rm-segment-border: #444444;
    -rm-segment-bg-hover: #3D3D3D;
    -rm-segment-border-hover: #555555;
    -rm-segment-bg-selected: #007ACC;
    -rm-segment-border-selected: #005A9E;
}
```

### Default Theme (Optional)

For applications not using AtlantaFX, a default theme is provided that defines the `-rm-*` variables with grayscale fallbacks. Load it as a user agent stylesheet:

```java
import javafx.application.Application;

public class MyApp extends Application {
    @Override
    public void start(Stage stage) {
        // Load default radial menu theme (optional, for non-AtlantaFX apps)
        Application.setUserAgentStylesheet(
            getClass().getResource("/ste/fx/control/radialmenu/radial-menu-theme.css").toExternalForm()
        );
        // ... rest of your code
    }
}
```

### AtlantaFX Theme (Optional)

For applications using AtlantaFX, an integration theme is provided that maps `-rm-*` variables to AtlantaFX button colors. Load it as a user agent stylesheet **after** setting the AtlantaFX theme:

```java
import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;

public class MyApp extends Application {
    @Override
    public void start(Stage stage) {
        // Apply AtlantaFX theme FIRST
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // Then load radial menu AtlantaFX integration (optional, but recommended)
        // This maps -rm-* variables to AtlantaFX button colors
        Application.setUserAgentStylesheet(
            getClass().getResource("/ste/fx/control/radialmenu/radial-menu-atlantafx.css").toExternalForm()
        );
        // ... rest of your code
    }
}
```

**Note**: The order matters — AtlantaFX theme must be loaded first so its `-color-button-*` variables are defined, then the radial menu integration theme maps them to `-rm-*`.

## AtlantaFX Theming

The component integrates with [AtlantaFX](https://github.com/mkpaz/atlantafx) themes. By default it uses AtlantaFX button color variables, so it automatically matches your application's theme.

### Style Mapping

The radial menu's visual states map to AtlantaFX button looked-up colors:

| Radial Menu State | AtlantaFX Variable | Description |
|-------------------|-------------------|-------------|
| Default | `-color-button-bg`, `-color-button-border` | Segment fill & stroke |
| Hover | `-color-button-bg-hover`, `-color-button-border-hover` | Segment fill & stroke on hover |
| Selected | `-color-button-bg-pressed`, `-color-button-border-pressed` | Segment fill & stroke when selected |

### Using with AtlantaFX

```java
import atlantafx.base.theme.PrimerLight; // or PrimerDark, Dracula, CupertinoLight, etc.
import javafx.application.Application;

public class MyApp extends Application {
    @Override
    public void start(Stage stage) {
        // Apply AtlantaFX theme BEFORE creating scene
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        // ... rest of your code
    }
}
```

### Available AtlantaFX Themes (base module)

| Theme Class | Variant |
|-------------|---------|
| `PrimerLight` | Light (default GitHub-like) |
| `PrimerDark` | Dark |
| `Dracula` | Dark (popular community theme) |
| `CupertinoLight` | Light (macOS-like) |
| `CupertinoDark` | Dark (macOS-like) |
| `NordLight` | Light (Nord color palette) |
| `NordDark` | Dark (Nord color palette) |

See [AtlantaFX themes documentation](https://mkpaz.github.io/atlantafx/) for complete list.

## Demo Application

Run the included demo to see the component in action:

```bash
mvn javafx:run
```

The demo shows:
- 5-segment radial menu with centered hub
- Selection callback updating center label
- Hover and selected visual states
- AtlantaFX PrimerLight theme applied

## Build & Test

```bash
# Compile
mvn compile

# Run tests (headless, Monocle)
mvn test

# Package
mvn package
```

## Requirements

- Java 21+
- JavaFX 21+
- Maven 3.8+

## License

Apache License 2.0