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
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.Path;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;
import static org.assertj.core.api.BDDAssertions.within;

class RadialMenuSizingTest {

    private static final double DEFAULT_RATIO = 60.0 / 135.0;

    private static MenuItem<String> item(String label) {
        return new MenuItem<>(label, label);
    }

    private static RadialMenu fourItems() {
        return new RadialMenu(List.of(item("Scope"), item("Story"), item("Code"), item("Explore")));
    }

    private static Path segment(RadialMenu menu, int index) {
        return (Path) menu.getChildren().get(index);
    }

    @BeforeAll
    static void startToolkit() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException alreadyRunning) {
        }
    }

    private static void applyCss(RadialMenu menu) {
        Pane root = new Pane(menu);
        new Scene(root, 500, 500);
        root.applyCss();
        root.layout();
    }

    @Test
    void defaults_preserve_the_current_footprint() {
        RadialMenu menu = fourItems();

        then(menu.diameter()).isEqualTo(270.0);
        then(menu.innerRadiusRatio()).isCloseTo(DEFAULT_RATIO, within(1e-9));
        then(menu.minWidth(270.0)).isEqualTo(270.0);
        then(menu.prefWidth(270.0)).isEqualTo(270.0);
        then(menu.maxWidth(270.0)).isEqualTo(270.0);
        then(menu.minHeight(270.0)).isEqualTo(270.0);
        then(menu.prefHeight(270.0)).isEqualTo(270.0);
        then(menu.maxHeight(270.0)).isEqualTo(270.0);
    }

    @Test
    void layout_bounds_are_the_diameter_square() {
        RadialMenu menu = fourItems();
        menu.resize(270, 270);
        menu.layout();

        then(menu.getLayoutBounds()).isEqualTo(new BoundingBox(0, 0, 270, 270));
        then(menu.getClip()).isInstanceOf(Rectangle.class);
        Rectangle clip = (Rectangle) menu.getClip();
        then(clip.getX()).isZero();
        then(clip.getY()).isZero();
        then(clip.getWidth()).isEqualTo(270.0);
        then(clip.getHeight()).isEqualTo(270.0);
    }

    @Test
    void diameter_can_be_bound() {
        RadialMenu menu = fourItems();
        SimpleDoubleProperty externalHeight = new SimpleDoubleProperty(180);

        menu.diameterProperty().bind(externalHeight);

        then(menu.diameter()).isEqualTo(180.0);
        then(menu.prefWidth(-1)).isEqualTo(180.0);
        then(menu.prefHeight(-1)).isEqualTo(180.0);
        then(menu.maxWidth(-1)).isEqualTo(180.0);
        then(menu.maxHeight(-1)).isEqualTo(180.0);
        menu.resize(180, 180);
        menu.layout();
        then(menu.getLayoutBounds()).isEqualTo(new BoundingBox(0, 0, 180, 180));
        MoveTo start = (MoveTo) segment(menu, 0).getElements().get(0);
        then(start.getX()).isCloseTo(90.0, within(1e-9));
        then(start.getY()).isCloseTo(0.0, within(1e-9));
    }

    @Test
    void diameter_changes_at_runtime_scale_geometry_and_keep_selection() {
        RadialMenu menu = fourItems();
        MenuItem<?> scope = menu.items().get(0);
        menu.selectItemForTesting(scope);

        menu.diameter(120);
        menu.resize(120, 120);
        menu.layout();

        then(menu.diameter()).isEqualTo(120.0);
        then(menu.getLayoutBounds()).isEqualTo(new BoundingBox(0, 0, 120, 120));
        then(segment(menu, 0).getPseudoClassStates()).extracting(pc -> pc.getPseudoClassName()).contains("selected");
        then(menu.selectedItem()).isSameAs(scope);
        MoveTo start = (MoveTo) segment(menu, 0).getElements().get(0);
        then(start.getX()).isCloseTo(60.0, within(1e-9));
        then(start.getY()).isCloseTo(0.0, within(1e-9));
    }

    @Test
    void selection_does_not_change_the_footprint() {
        RadialMenu menu = fourItems();
        menu.resize(270, 270);
        menu.layout();
        Bounds before = menu.getLayoutBounds();

        menu.selectItemForTesting(menu.items().get(2));
        menu.layout();

        then(menu.getLayoutBounds()).isEqualTo(before);
    }

    @Test
    void all_wedge_geometry_stays_inside_the_square() {
        RadialMenu menu = new RadialMenu(List.of(
                item("A"), item("B"), item("C"), item("D"), item("E"), item("F"), item("G"), item("H")));
        menu.diameter(180);
        menu.resize(180, 180);
        menu.layout();

        for (int i = 0; i < 8; i++) {
            Path path = segment(menu, i);
            for (javafx.scene.shape.PathElement element : path.getElements()) {
                Point2D point = pointOf(element);
                if (point == null) continue;
                then(point.getX()).isBetween(0.0, 180.0);
                then(point.getY()).isBetween(0.0, 180.0);
            }
        }
    }

    private static Point2D pointOf(javafx.scene.shape.PathElement element) {
        if (element instanceof MoveTo moveTo) return new Point2D(moveTo.getX(), moveTo.getY());
        if (element instanceof LineTo lineTo) return new Point2D(lineTo.getX(), lineTo.getY());
        if (element instanceof ArcTo arcTo) return new Point2D(arcTo.getX(), arcTo.getY());
        return null;
    }

    @Test
    void inner_radius_ratio_is_clamped_on_write() {
        RadialMenu menu = fourItems();

        menu.innerRadiusRatio(-0.2);
        then(menu.innerRadiusRatio()).isZero();

        menu.innerRadiusRatio(1.5);
        then(menu.innerRadiusRatio()).isEqualTo(Math.nextDown(1.0));
    }

    @Test
    void bound_inner_radius_ratio_is_reported_clamped() {
        RadialMenu menu = fourItems();
        SimpleDoubleProperty external = new SimpleDoubleProperty(1.5);

        menu.innerRadiusRatioProperty().bind(external);

        then(menu.innerRadiusRatio()).isEqualTo(Math.nextDown(1.0));

        external.set(-1.0);
        then(menu.innerRadiusRatio()).isZero();
    }

    @Test
    void inner_radius_ratio_changes_the_hub_without_resizing() {
        RadialMenu menu = fourItems();
        menu.resize(270, 270);

        menu.innerRadiusRatio(0.25);

        then(menu.getLayoutBounds()).isEqualTo(new BoundingBox(0, 0, 270, 270));
        ArcTo innerStart = (ArcTo) segment(menu, 0).getElements().get(3);
        then(innerStart.getX()).isCloseTo(135.0, within(1e-9));
        then(innerStart.getY()).isCloseTo(135.0 - 33.75, within(1e-9));
    }

    @Test
    void non_positive_diameter_renders_nothing_and_keeps_non_negative_size() {
        RadialMenu menu = fourItems();

        menu.diameter(0);
        then(menu.labels()).isEmpty();
        then(menu.getChildren()).allMatch(node -> node instanceof javafx.scene.Group);
        then(menu.prefWidth(-1)).isZero();

        menu.diameter(-50);
        then(menu.prefWidth(-1)).isZero();
        then(menu.prefHeight(-1)).isZero();
    }

    @Test
    void default_label_font_follows_the_diameter() {
        RadialMenu menu = fourItems();

        menu.diameter(135);

        then(menu.labelFontSize()).isCloseTo(6.75, within(1e-9));
        then(menu.labels()).isNotEmpty();
        then(menu.labels().get(0).getFont().getSize()).isCloseTo(6.75, within(0.01));
    }

    @Test
    void explicit_label_font_size_stays_fixed_across_resizes() {
        RadialMenu menu = new RadialMenu(List.of(
                item("A very long menu label that cannot possibly fit its wedge"),
                item("Story"), item("Code"), item("Explore")));
        menu.labelFontSize(18.0);

        menu.diameter(120);

        Text label = menu.labels().get(0);
        then(label.getFont().getSize()).isCloseTo(18.0, within(0.01));
        then(label.getText()).endsWith("\u2026");
    }

    @Test
    void labels_follow_the_resized_band() {
        RadialMenu menu = fourItems();

        menu.diameter(180);
        menu.resize(180, 180);
        menu.layout();

        double outer = 90.0;
        double inner = outer * DEFAULT_RATIO;
        double radius = inner + 0.5 * (outer - inner);
        for (int i = 0; i < menu.labels().size(); i++) {
            double angle = Math.toRadians(-90.0 + (i + 0.5) * 90.0);
            Point2D expected = new Point2D(
                    90.0 + radius * Math.cos(angle), 90.0 + radius * Math.sin(angle));
            Text label = menu.labels().get(i);
            Bounds bounds = label.localToScene(label.getLayoutBounds());
            Point2D center = new Point2D(bounds.getMinX() + bounds.getWidth() / 2.0,
                    bounds.getMinY() + bounds.getHeight() / 2.0);
            then(center.distance(expected)).isLessThan(0.5);
        }
    }

    @Test
    void labels_stay_mouse_transparent_after_resize() {
        RadialMenu menu = fourItems();

        menu.diameter(180);

        then(menu.labelLayer().isMouseTransparent()).isTrue();
        then(menu.labels()).allMatch(text -> text.getParent().isMouseTransparent());
    }

    @Test
    void selection_callback_works_after_resize() {
        MenuItem<?>[] received = new MenuItem[1];
        RadialMenu menu = new RadialMenu(
                List.of(item("Scope"), item("Story"), item("Code"), item("Explore")),
                selected -> received[0] = selected);

        menu.diameter(150);
        menu.selectItemForTesting(menu.items().get(3));

        then(received[0]).isSameAs(menu.items().get(3));
        then(menu.selectedItem()).isSameAs(menu.items().get(3));
    }

    @Test
    void stroke_width_is_uniform_across_states() {
        RadialMenu menu = fourItems();
        applyCss(menu);

        then(segment(menu, 0).getStrokeWidth()).isEqualTo(2.0);

        menu.selectItemForTesting(menu.items().get(0));
        applyCss(menu);

        then(segment(menu, 0).getPseudoClassStates()).extracting(pc -> pc.getPseudoClassName()).contains("selected");
        then(segment(menu, 0).getStrokeWidth()).isEqualTo(2.0);
    }
}
