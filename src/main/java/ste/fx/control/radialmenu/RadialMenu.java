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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Path;

import java.util.List;

public class RadialMenu extends Pane {
    private final ObservableList<MenuItem<?>> items;
    private final ObservableList<Node> segments;
    private MenuItemSelectionCallback selectionCallback;
    private MenuItem<?> selectedItem;
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    public RadialMenu(List<? extends MenuItem<?>> items) {
        this(items, null);
    }

    public RadialMenu(List<? extends MenuItem<?>> items, MenuItemSelectionCallback selectionCallback) {
        this.items = FXCollections.observableArrayList(items);
        this.segments = FXCollections.observableArrayList();
        this.selectionCallback = selectionCallback;
        getStylesheets().add(getClass().getResource("/com/example/radialmenu/radial-menu.css").toExternalForm());
        buildSegments();
    }

    public void setSelectionCallback(MenuItemSelectionCallback callback) {
        this.selectionCallback = callback;
    }

    public MenuItem<?> selectedItem() {
        return selectedItem;
    }

    private void buildSegments() {
        segments.clear();
        double innerRadius = 75.0;
        double outerRadius = 135.0;
        int totalSegments = items.size();
        if (totalSegments == 0) return;

        double angleStep = 360.0 / totalSegments;
        double startAngle = -90.0;

        for (int i = 0; i < totalSegments; i++) {
            MenuItem<?> item = items.get(i);
            Path segment = createSegment(innerRadius, outerRadius, startAngle + (i * angleStep), angleStep);
            segment.getStyleClass().add("radial-menu-segment");
            final MenuItem<?> finalItem = item;
            segment.setOnMouseClicked(e -> selectItem(finalItem));
            segments.add(segment);
        }
        getChildren().setAll(segments);
    }

    private void selectItem(MenuItem<?> item) {
        selectedItem = item;
        for (Node node : segments) {
            MenuItem<?> mi = items.get(segments.indexOf(node));
            node.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, mi == selectedItem);
        }
        if (selectionCallback != null) {
            selectionCallback.onSelection(item);
        }
    }

    // Package-private for testing
    void selectItemForTesting(MenuItem<?> item) {
        selectItem(item);
    }

    private Path createSegment(double innerR, double outerR, double startAngleDegrees, double sweepAngleDegrees) {
        Path path = new Path();
        double alphaStart = Math.toRadians(startAngleDegrees);
        double alphaEnd = Math.toRadians(startAngleDegrees + sweepAngleDegrees);

        double p1x = outerR * Math.cos(alphaStart);
        double p1y = outerR * Math.sin(alphaStart);
        double p2x = outerR * Math.cos(alphaEnd);
        double p2y = outerR * Math.sin(alphaEnd);
        double p3x = innerR * Math.cos(alphaEnd);
        double p3y = innerR * Math.sin(alphaEnd);
        double p4x = innerR * Math.cos(alphaStart);
        double p4y = innerR * Math.sin(alphaStart);

        path.getElements().add(new javafx.scene.shape.MoveTo(p1x, p1y));
        path.getElements().add(new javafx.scene.shape.ArcTo(outerR, outerR, 0, p2x, p2y, false, true));
        path.getElements().add(new javafx.scene.shape.LineTo(p3x, p3y));
        path.getElements().add(new javafx.scene.shape.ArcTo(innerR, innerR, 0, p4x, p4y, false, false));
        path.getElements().add(new javafx.scene.shape.ClosePath());

        return path;
    }

    public ObservableList<MenuItem<?>> items() {
        return items;
    }
}