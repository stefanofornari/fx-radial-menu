/*
 * Copyright 2026 the original author or authors from the fx-radial-menu project (https://github.com/stefanofornari/fx-radial-menu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ste.fx.control.radialmenu;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.within;

class RadialMenuLabelsTest {

    private static final double INNER_RADIUS = 60.0;
    private static final double OUTER_RADIUS = 135.0;
    private static final double MID_RADIUS = (INNER_RADIUS + OUTER_RADIUS) / 2.0;
    private static final double CENTER = 135.0;

    private static MenuItem<String> item(String label) {
        return new MenuItem<>(label, label);
    }

    @BeforeAll
    static void startToolkit() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException alreadyRunning) {
        }
    }

    private static Scene style(RadialMenu menu, String css) throws Exception {
        Path stylesheet = Files.createTempFile("radial-menu-labels", ".css");
        Files.writeString(stylesheet, css);
        stylesheet.toFile().deleteOnExit();
        Pane root = new Pane(menu);
        Scene scene = new Scene(root, 500, 500);
        scene.getStylesheets().add(stylesheet.toUri().toString());
        root.applyCss();
        root.layout();
        return scene;
    }

    private static RadialMenu fourItems() {
        return new RadialMenu(List.of(item("Scope"), item("Story"), item("Code"), item("Explore")));
    }

    private static Point2D center(Text text) {
        Bounds bounds = text.localToScene(text.getLayoutBounds());
        return new Point2D(
                bounds.getMinX() + bounds.getWidth() / 2.0,
                bounds.getMinY() + bounds.getHeight() / 2.0);
    }

    private static Point2D expectedAnchor(int index, int total, double ratio) {
        double step = 360.0 / total;
        double angle = Math.toRadians(-90.0 + (index + 0.5) * step);
        double radius = INNER_RADIUS + ratio * (OUTER_RADIUS - INNER_RADIUS);
        return new Point2D(CENTER + radius * Math.cos(angle), CENTER + radius * Math.sin(angle));
    }

    @Test
    void labels_render_for_every_item() {
        RadialMenu menu = fourItems();

        then(menu.labels()).hasSize(4);
    }

    @Test
    void labels_use_the_required_style_class() {
        RadialMenu menu = fourItems();

        then(menu.labels()).allMatch(text -> text.getStyleClass().contains("radial-menu-label"));
    }

    @Test
    void labels_are_upright_and_centred_on_the_wedge_mid_angle() {
        RadialMenu menu = fourItems();

        for (int i = 0; i < menu.labels().size(); i++) {
            Text label = menu.labels().get(i);
            then(label.getRotate()).isZero();
            then(center(label).distance(expectedAnchor(i, 4, 0.5))).isLessThan(0.5);
        }
    }

    @Test
    void labels_use_the_default_band_midpoint() {
        RadialMenu menu = fourItems();

        then(menu.labelRadiusRatio()).isEqualTo(0.5);
        then(center(menu.labels().get(0)).distance(expectedAnchor(0, 4, 0.5))).isLessThan(0.5);
    }

    @Test
    void changing_label_radius_ratio_repositions_without_rebuilding() {
        RadialMenu menu = fourItems();
        List<Text> before = List.copyOf(menu.labels());

        menu.labelRadiusRatio(0.75);

        then(menu.labels()).containsExactlyElementsOf(before);
        for (int i = 0; i < menu.labels().size(); i++) {
            then(center(menu.labels().get(i)).distance(expectedAnchor(i, 4, 0.75))).isLessThan(0.5);
        }
    }

    @Test
    void label_radius_ratio_is_clamped() {
        RadialMenu menu = fourItems();

        menu.labelRadiusRatio(-1.0);
        then(menu.labelRadiusRatio()).isZero();

        menu.labelRadiusRatio(2.0);
        then(menu.labelRadiusRatio()).isEqualTo(1.0);
    }

    @Test
    void labels_are_mouse_transparent() {
        RadialMenu menu = fourItems();

        then(menu.labelLayer().isMouseTransparent()).isTrue();
        then(menu.labels()).allMatch(text -> text.getParent().isMouseTransparent());
    }

    @Test
    void empty_menu_renders_no_labels() {
        RadialMenu menu = new RadialMenu(List.of());

        then(menu.labels()).isEmpty();
    }

    @Test
    void blank_labels_are_not_rendered() {
        RadialMenu menu = new RadialMenu(List.of(item("   "), item("Code")));

        then(menu.labels()).hasSize(1);
        then(menu.labels().get(0).getText()).isEqualTo("Code");
    }

    @Test
    void long_labels_shrink_to_seventy_percent_then_truncate() {
        String longLabel = "A very long menu label that cannot possibly fit its wedge";
        RadialMenu menu = new RadialMenu(List.of(item(longLabel), item("Story"), item("Code"), item("Explore")));

        Text label = menu.labels().get(0);

        then(label.getText()).endsWith("\u2026");
        then(label.getFont().getSize()).isCloseTo(13.5 * 0.7, within(0.01));
        then(label.getLayoutBounds().getWidth()).isLessThanOrEqualTo(130.0);
    }

    @Test
    void explicit_font_size_disables_auto_shrink() {
        String longLabel = "A very long menu label that cannot possibly fit its wedge";
        RadialMenu menu = new RadialMenu(List.of(item(longLabel), item("Story"), item("Code"), item("Explore")));

        menu.labelFontSize(18.0);

        Text label = menu.labels().get(0);
        then(label.getFont().getSize()).isCloseTo(18.0, within(0.01));
        then(label.getText()).endsWith("\u2026");
    }

    @Test
    void font_size_is_styleable_and_disables_shrink() throws Exception {
        RadialMenu menu = new RadialMenu(List.of(
                item("A very long menu label that cannot possibly fit its wedge"),
                item("Story"), item("Code"), item("Explore")));

        style(menu, ".radial-menu { -rm-label-font-size: 18px; }");

        then(menu.labelFontSize()).isCloseTo(18.0, within(0.01));
        then(menu.labels().get(0).getFont().getSize()).isCloseTo(18.0, within(0.01));
        then(menu.labels().get(0).getText()).endsWith("\u2026");
    }

    @Test
    void radius_ratio_is_styleable_from_css() throws Exception {
        RadialMenu menu = fourItems();

        style(menu, ".radial-menu { -rm-label-radius-ratio: 0.75; }");

        then(menu.labelRadiusRatio()).isCloseTo(0.75, within(0.001));
        for (int i = 0; i < menu.labels().size(); i++) {
            then(center(menu.labels().get(i)).distance(expectedAnchor(i, 4, 0.75))).isLessThan(0.5);
        }
    }

    @Test
    void bound_radius_ratio_wins_over_css() throws Exception {
        RadialMenu menu = fourItems();
        menu.labelRadiusRatioProperty().bind(new javafx.beans.property.SimpleDoubleProperty(0.25));

        style(menu, ".radial-menu { -rm-label-radius-ratio: 0.75; }");

        then(menu.labelRadiusRatio()).isCloseTo(0.25, within(0.001));
    }

    @Test
    void label_fill_is_styleable() throws Exception {
        RadialMenu menu = fourItems();

        style(menu, ".radial-menu { -rm-label-fill: red; }");

        then(menu.labels()).allMatch(label -> Color.RED.equals(label.getFill()));
    }

    @Test
    void items_changes_rebuild_labels() {
        RadialMenu menu = new RadialMenu(List.of(item("Scope"), item("Story")));

        menu.items().add(item("Extra"));
        then(menu.labels()).hasSize(3);
        then(menu.labels()).extracting(Text::getText).containsExactly("Scope", "Story", "Extra");

        menu.items().remove(0);
        then(menu.labels()).extracting(Text::getText).containsExactly("Story", "Extra");
    }

    @Test
    void empty_items_after_change_render_no_labels() {
        RadialMenu menu = new RadialMenu(List.of(item("Scope")));

        menu.items().clear();

        then(menu.labels()).isEmpty();
        then(menu.getChildren()).allMatch(node -> node instanceof Group);
    }

    @Test
    void layout_keeps_labels_at_their_anchors() {
        RadialMenu menu = fourItems();
        menu.labelRadiusRatio(0.75);

        menu.resize(500, 500);
        menu.layout();

        for (int i = 0; i < menu.labels().size(); i++) {
            then(center(menu.labels().get(i)).distance(expectedAnchor(i, 4, 0.75))).isLessThan(0.5);
        }
    }

    @Test
    void icon_is_rendered_above_a_fitting_label() {
        Circle icon = new Circle(4);
        MenuItem<String> withIcon = new MenuItem<>("Code", icon, "code");
        RadialMenu menu = new RadialMenu(List.of(withIcon, item("Story"), item("Code"), item("Explore")));

        Group group = (Group) menu.labels().get(0).getParent();

        then(group.getChildren()).containsExactly(icon, menu.labels().get(0));
        then(icon.getScaleX()).isEqualTo(icon.getScaleY());
    }

    @Test
    void icon_only_fallback_when_icon_and_label_do_not_fit() {
        Circle icon = new Circle(80);
        MenuItem<String> withIcon = new MenuItem<>(
                "A very long menu label that cannot possibly fit its wedge", icon, "code");
        RadialMenu menu = new RadialMenu(List.of(withIcon, item("Story"), item("Code"), item("Explore")));

        List<Node> rendered = menu.labelLayer().getChildren();

        then(menu.labels()).hasSize(3);
        then(rendered).hasSize(4);
        then(((Group) rendered.get(0)).getChildren()).hasSize(1);
        Node renderedIcon = ((Group) rendered.get(0)).getChildren().get(0);
        then(renderedIcon).isSameAs(icon);
        then(icon.getScaleX()).isEqualTo(icon.getScaleY());
        then(icon.getBoundsInParent().getWidth()).isLessThanOrEqualTo(130.0);
        then(icon.getBoundsInParent().getHeight()).isLessThanOrEqualTo(67.0);
    }
}
