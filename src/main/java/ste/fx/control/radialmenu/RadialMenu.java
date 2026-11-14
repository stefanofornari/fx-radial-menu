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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RadialMenu extends Pane {
    private static final double DEFAULT_DIAMETER = 270.0;
    private static final double DEFAULT_INNER_RADIUS_RATIO = 60.0 / 135.0;
    private static final double DEFAULT_LABEL_RADIUS_RATIO = 0.5;
    private static final double DEFAULT_LABEL_FONT_SIZE = DEFAULT_DIAMETER * 0.05;
    private static final double LABEL_MARGIN = 4.0;
    private static final double ICON_GAP = 2.0;
    private static final double ICON_HEIGHT_RATIO = 0.45;
    private static final double MIN_LABEL_FONT_RATIO = 0.7;
    private static final String ELLIPSIS = "\u2026";
    private static final PseudoClass SELECTED_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

    private final ObservableList<MenuItem<?>> items;
    private final ObservableList<Node> segments;
    private final ObservableList<Text> labels;
    private final Group labelLayer;
    private final DiameterProperty diameter;
    private final InnerRadiusRatioProperty innerRadiusRatio;
    private final FontSizeProperty labelFontSize;
    private final RatioProperty labelRadiusRatio;
    private MenuItemSelectionCallback selectionCallback;
    private MenuItem<?> selectedItem;

    public RadialMenu(List<? extends MenuItem<?>> items) {
        this(items, null);
    }

    public RadialMenu(List<? extends MenuItem<?>> items, MenuItemSelectionCallback selectionCallback) {
        this.items = FXCollections.observableArrayList(items);
        this.segments = FXCollections.observableArrayList();
        this.labels = FXCollections.observableArrayList();
        this.labelLayer = new Group();
        this.labelLayer.getStyleClass().add("radial-menu-label-layer");
        this.labelLayer.setMouseTransparent(true);
        this.diameter = new DiameterProperty(DEFAULT_DIAMETER);
        this.innerRadiusRatio = new InnerRadiusRatioProperty(DEFAULT_INNER_RADIUS_RATIO);
        this.labelFontSize = new FontSizeProperty(
                LABEL_FONT_SIZE_META, "labelFontSize", DEFAULT_LABEL_FONT_SIZE);
        this.labelRadiusRatio = new RatioProperty(
                LABEL_RADIUS_RATIO_META, "labelRadiusRatio", DEFAULT_LABEL_RADIUS_RATIO);
        this.selectionCallback = selectionCallback;
        getStyleClass().add("radial-menu");
        getStylesheets().add(getClass().getResource("/ste/fx/control/radialmenu/radial-menu.css").toExternalForm());
        this.items.addListener((ListChangeListener<MenuItem<?>>) change -> rebuild());
        this.diameter.addListener((obs, oldValue, newValue) -> {
            updateSizing();
            if (!labelFontSize.explicit() && !labelFontSize.isBound()) {
                labelFontSize.setDerived(defaultLabelFontSize());
            }
            rebuild();
        });
        this.innerRadiusRatio.addListener((obs, oldValue, newValue) -> rebuild());
        this.labelRadiusRatio.addListener((obs, oldValue, newValue) -> positionLabels());
        this.labelFontSize.addListener((obs, oldValue, newValue) -> buildLabels());
        updateSizing();
        buildSegments();
    }

    public void setSelectionCallback(MenuItemSelectionCallback callback) {
        this.selectionCallback = callback;
    }

    public MenuItem<?> selectedItem() {
        return selectedItem;
    }

    public final DoubleProperty diameterProperty() {
        return diameter;
    }

    public final double diameter() {
        return diameter.get();
    }

    public final void diameter(double value) {
        diameter.set(value);
    }

    public final DoubleProperty innerRadiusRatioProperty() {
        return innerRadiusRatio;
    }

    public final double innerRadiusRatio() {
        return innerRadiusRatio.get();
    }

    public final void innerRadiusRatio(double value) {
        innerRadiusRatio.set(value);
    }

    public final DoubleProperty labelRadiusRatioProperty() {
        return labelRadiusRatio;
    }

    public final double labelRadiusRatio() {
        return labelRadiusRatio.get();
    }

    public final void labelRadiusRatio(double value) {
        labelRadiusRatio.set(value);
    }

    public final DoubleProperty labelFontSizeProperty() {
        return labelFontSize;
    }

    public final double labelFontSize() {
        return labelFontSize.get();
    }

    public final void labelFontSize(double value) {
        labelFontSize.set(value);
    }

    public ObservableList<Text> labels() {
        return labels;
    }

    Group labelLayer() {
        return labelLayer;
    }

    private void rebuild() {
        if (selectedItem != null && !items.contains(selectedItem)) {
            selectedItem = null;
        }
        buildSegments();
    }

    private void buildSegments() {
        segments.clear();
        int totalSegments = items.size();
        double effectiveDiameter = effectiveDiameter();
        if (totalSegments > 0 && effectiveDiameter > 0.0) {
            double angleStep = 360.0 / totalSegments;
            double startAngle = -90.0;
            double center = effectiveDiameter / 2.0;

            for (int i = 0; i < totalSegments; i++) {
                MenuItem<?> item = items.get(i);
                Path segment = createSegment(innerRadius(), outerRadius(), center, center,
                        startAngle + (i * angleStep), angleStep);
                segment.getStyleClass().add("radial-menu-segment");
                final MenuItem<?> finalItem = item;
                segment.setOnMouseClicked(e -> selectItem(finalItem));
                if (item == selectedItem) {
                    segment.pseudoClassStateChanged(SELECTED_PSEUDO_CLASS, true);
                }
                segments.add(segment);
            }
        }
        buildLabels();
        getChildren().setAll(segments);
        getChildren().add(labelLayer);
    }

    private double effectiveDiameter() {
        return Math.max(0.0, diameter.get());
    }

    private double outerRadius() {
        return effectiveDiameter() / 2.0;
    }

    private double innerRadius() {
        return outerRadius() * innerRadiusRatio();
    }

    private double defaultLabelFontSize() {
        return effectiveDiameter() * 0.05;
    }

    private void updateSizing() {
        double effectiveDiameter = effectiveDiameter();
        setMinSize(effectiveDiameter, effectiveDiameter);
        setPrefSize(effectiveDiameter, effectiveDiameter);
        setMaxSize(effectiveDiameter, effectiveDiameter);
        Rectangle clip;
        if (getClip() instanceof Rectangle existingClip) {
            clip = existingClip;
        } else {
            clip = new Rectangle();
            setClip(clip);
        }
        clip.setX(0.0);
        clip.setY(0.0);
        clip.setWidth(effectiveDiameter);
        clip.setHeight(effectiveDiameter);
    }

    private void buildLabels() {
        labelLayer.getChildren().clear();
        labels.clear();
        int totalSegments = items.size();
        if (totalSegments == 0 || effectiveDiameter() <= 0.0) return;

        double angleStep = 360.0 / totalSegments;
        double midRadius = labelMidRadius();
        double chord = totalSegments == 1
                ? 2.0 * midRadius
                : 2.0 * midRadius * Math.sin(Math.toRadians(angleStep / 2.0));
        double maxWidth = Math.max(0.0, Math.min(chord, 2.0 * midRadius) - 2.0 * LABEL_MARGIN);
        double maxHeight = Math.max(0.0, (outerRadius() - innerRadius()) - 2.0 * LABEL_MARGIN);

        for (int i = 0; i < totalSegments; i++) {
            MenuItem<?> item = items.get(i);
            Group wedgeLabel = createWedgeLabel(item, maxWidth, maxHeight);
            if (wedgeLabel != null) {
                wedgeLabel.setUserData(i);
                labelLayer.getChildren().add(wedgeLabel);
            }
        }
        positionLabels();
    }

    private Group createWedgeLabel(MenuItem<?> item, double maxWidth, double maxHeight) {
        String text = item.label();
        boolean hasText = text != null && !text.isBlank();
        Node icon = item.icon();
        if (!hasText && icon == null) return null;

        Group group = new Group();
        group.setMouseTransparent(true);
        Text label = hasText ? createLabelText(text, maxWidth, maxHeight) : null;
        if (label == null && icon == null) return null;

        if (icon == null) {
            group.getChildren().add(label);
            labels.add(label);
            return group;
        }

        Bounds naturalIcon = icon.getLayoutBounds();
        double combinedIconHeight = Math.max(0.0, maxHeight * ICON_HEIGHT_RATIO - ICON_GAP);
        boolean iconFitsCombined = naturalIcon.getWidth() <= maxWidth
                && naturalIcon.getHeight() <= combinedIconHeight;

        if (label == null) {
            Bounds iconBounds = scaleIcon(icon, maxWidth, maxHeight);
            group.getChildren().add(icon);
            placeInGroup(icon, iconBounds, 0.0, 0.0);
            return group;
        }

        Bounds iconBounds = iconFitsCombined
                ? scaleIcon(icon, maxWidth, combinedIconHeight)
                : icon.getBoundsInParent();
        boolean fitsTogether = iconFitsCombined
                && iconBounds.getHeight() + ICON_GAP + label.getLayoutBounds().getHeight() <= maxHeight
                && Math.max(iconBounds.getWidth(), label.getLayoutBounds().getWidth()) <= maxWidth;
        if (!fitsTogether) {
            iconBounds = scaleIcon(icon, maxWidth, maxHeight);
            group.getChildren().add(icon);
            placeInGroup(icon, iconBounds, 0.0, 0.0);
            return group;
        }

        group.getChildren().add(icon);
        if (label != null) {
            group.getChildren().add(label);
            labels.add(label);
            Bounds textBounds = label.getLayoutBounds();
            double contentWidth = Math.max(iconBounds.getWidth(), textBounds.getWidth());
            double textX = (contentWidth - textBounds.getWidth()) / 2.0 - textBounds.getMinX();
            double baselineY = 0.0;
            label.setX(textX);
            label.setY(baselineY);
            double iconX = (contentWidth - iconBounds.getWidth()) / 2.0;
            double iconY = textBounds.getMinY() - ICON_GAP - iconBounds.getHeight();
            placeInGroup(icon, iconBounds, iconX, iconY);
        }
        return group;
    }

    private Text createLabelText(String label, double maxWidth, double maxHeight) {
        double baseSize = labelFontSize();
        Font font = Font.font(baseSize);
        String shown = label;

        if (!fontSizeExplicitlySet() && !fits(label, font, maxWidth, maxHeight)) {
            font = Font.font(baseSize * MIN_LABEL_FONT_RATIO);
        }
        if (!fits(shown, font, maxWidth, maxHeight)) {
            shown = ellipsize(label, font, maxWidth);
            if (shown.isEmpty()) return null;
        }

        Text text = new Text(shown);
        text.setFont(font);
        text.getStyleClass().add("radial-menu-label");
        return text;
    }

    private boolean fits(String value, Font font, double maxWidth, double maxHeight) {
        Bounds bounds = measure(value, font);
        return bounds.getWidth() <= maxWidth && bounds.getHeight() <= maxHeight;
    }

    private Bounds measure(String value, Font font) {
        Text probe = new Text(value);
        probe.setFont(font);
        return probe.getLayoutBounds();
    }

    private String ellipsize(String value, Font font, double maxWidth) {
        if (measure(ELLIPSIS, font).getWidth() > maxWidth) return "";
        int low = 0;
        int high = value.length();
        while (low < high) {
            int mid = (low + high + 1) / 2;
            String candidate = value.substring(0, mid).stripTrailing() + ELLIPSIS;
            if (measure(candidate, font).getWidth() <= maxWidth) {
                low = mid;
            } else {
                high = mid - 1;
            }
        }
        if (low == 0) return "";
        return value.substring(0, low).stripTrailing() + ELLIPSIS;
    }

    private Bounds scaleIcon(Node icon, double maxWidth, double maxHeight) {
        Bounds natural = icon.getLayoutBounds();
        if (natural.getWidth() <= 0.0 || natural.getHeight() <= 0.0 || maxWidth <= 0.0 || maxHeight <= 0.0) {
            icon.setScaleX(0.0);
            icon.setScaleY(0.0);
            return icon.getBoundsInParent();
        }
        double scale = Math.min(maxWidth / natural.getWidth(), maxHeight / natural.getHeight());
        icon.setScaleX(scale);
        icon.setScaleY(scale);
        return icon.getBoundsInParent();
    }

    private void placeInGroup(Node node, Bounds bounds, double x, double y) {
        node.setLayoutX(x - bounds.getMinX());
        node.setLayoutY(y - bounds.getMinY());
    }

    private boolean fontSizeExplicitlySet() {
        return labelFontSize.explicit();
    }

    private double labelMidRadius() {
        return innerRadius() + labelRadiusRatio() * (outerRadius() - innerRadius());
    }

    private void positionLabels() {
        int totalSegments = items.size();
        if (totalSegments == 0) return;
        double angleStep = 360.0 / totalSegments;
        double radius = labelMidRadius();
        double center = effectiveDiameter() / 2.0;
        for (Node wedgeLabel : labelLayer.getChildren()) {
            int index = (Integer) wedgeLabel.getUserData();
            double angle = Math.toRadians(-90.0 + (index + 0.5) * angleStep);
            Bounds bounds = wedgeLabel.getLayoutBounds();
            double anchorX = center + radius * Math.cos(angle);
            double anchorY = center + radius * Math.sin(angle);
            wedgeLabel.setLayoutX(anchorX - bounds.getMinX() - bounds.getWidth() / 2.0);
            wedgeLabel.setLayoutY(anchorY - bounds.getMinY() - bounds.getHeight() / 2.0);
        }
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        positionLabels();
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

    private Path createSegment(double innerR, double outerR, double centerX, double centerY,
                               double startAngleDegrees, double sweepAngleDegrees) {
        Path path = new Path();
        double alphaStart = Math.toRadians(startAngleDegrees);
        double alphaEnd = Math.toRadians(startAngleDegrees + sweepAngleDegrees);

        double p1x = centerX + outerR * Math.cos(alphaStart);
        double p1y = centerY + outerR * Math.sin(alphaStart);
        double p2x = centerX + outerR * Math.cos(alphaEnd);
        double p2y = centerY + outerR * Math.sin(alphaEnd);
        double p3x = centerX + innerR * Math.cos(alphaEnd);
        double p3y = centerY + innerR * Math.sin(alphaEnd);
        double p4x = centerX + innerR * Math.cos(alphaStart);
        double p4y = centerY + innerR * Math.sin(alphaStart);

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

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return STYLEABLES;
    }

    private static final CssMetaData<RadialMenu, Number> LABEL_RADIUS_RATIO_META =
            new CssMetaData<RadialMenu, Number>("-rm-label-radius-ratio", SizeConverter.getInstance(), DEFAULT_LABEL_RADIUS_RATIO) {
                @Override
                public boolean isSettable(RadialMenu node) {
                    return !node.labelRadiusRatio.isBound();
                }

                @Override
                public StyleableProperty<Number> getStyleableProperty(RadialMenu node) {
                    return node.labelRadiusRatio;
                }
            };

    private static final CssMetaData<RadialMenu, Number> LABEL_FONT_SIZE_META =
            new CssMetaData<RadialMenu, Number>("-rm-label-font-size", SizeConverter.getInstance(), DEFAULT_LABEL_FONT_SIZE) {
                @Override
                public boolean isSettable(RadialMenu node) {
                    return !node.labelFontSize.isBound();
                }

                @Override
                public StyleableProperty<Number> getStyleableProperty(RadialMenu node) {
                    return node.labelFontSize;
                }
            };

    private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    static {
        List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Pane.getClassCssMetaData());
        styleables.add(LABEL_RADIUS_RATIO_META);
        styleables.add(LABEL_FONT_SIZE_META);
        STYLEABLES = Collections.unmodifiableList(styleables);
    }

    private static final class DiameterProperty extends SimpleDoubleProperty {
        DiameterProperty(double initialValue) {
            super(initialValue);
        }

        @Override
        public void set(double value) {
            super.set(Math.max(0.0, value));
        }
    }

    private static final class InnerRadiusRatioProperty extends SimpleDoubleProperty {
        InnerRadiusRatioProperty(double initialValue) {
            super(initialValue);
        }

        @Override
        public double get() {
            return clamp(super.get());
        }

        @Override
        public void set(double value) {
            super.set(clamp(value));
        }

        private static double clamp(double value) {
            if (Double.isNaN(value)) return 0.0;
            return Math.max(0.0, Math.min(Math.nextDown(1.0), value));
        }
    }

    private static final class RatioProperty extends SimpleStyleableDoubleProperty {
        RatioProperty(CssMetaData<? extends Styleable, Number> metaData, String name, double initialValue) {
            super(metaData, null, name, initialValue);
        }

        @Override
        public void set(double value) {
            super.set(clamp(value));
        }

        @Override
        public void setValue(Number value) {
            super.setValue(clamp(value == null ? 0.0 : value.doubleValue()));
        }

        @Override
        public void applyStyle(StyleOrigin origin, Number value) {
            super.applyStyle(origin, clamp(value == null ? 0.0 : value.doubleValue()));
        }

        private static double clamp(double value) {
            if (Double.isNaN(value)) return 0.0;
            return Math.max(0.0, Math.min(1.0, value));
        }
    }

    private static final class FontSizeProperty extends SimpleStyleableDoubleProperty {
        private boolean explicit;

        FontSizeProperty(CssMetaData<? extends Styleable, Number> metaData, String name, double initialValue) {
            super(metaData, null, name, initialValue);
        }

        @Override
        public void set(double value) {
            explicit = true;
            super.set(value);
        }

        @Override
        public void setValue(Number value) {
            if (value != null) explicit = true;
            super.setValue(value);
        }

        @Override
        public void applyStyle(StyleOrigin origin, Number value) {
            if (value != null) explicit = true;
            super.applyStyle(origin, value);
        }

        void setDerived(double value) {
            super.set(value);
        }

        boolean explicit() {
            return explicit;
        }
    }
}
