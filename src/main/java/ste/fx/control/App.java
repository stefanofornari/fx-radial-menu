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
package ste.fx.control;

import atlantafx.base.theme.PrimerLight;
import ste.fx.control.radialmenu.MenuItem;
import ste.fx.control.radialmenu.RadialMenu;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Apply AtlantaFX theme
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        Pane canvasPane = new Pane();
        canvasPane.getStyleClass().add("demo-root");

        Label centerLabel = new Label("TOUCH");
        centerLabel.getStyleClass().add("demo-center-label");

        var items = java.util.List.of(
            new MenuItem<>("Wedge A", "actionA"),
            new MenuItem<>("Wedge B", "actionB"),
            new MenuItem<>("Wedge C", "actionC"),
            new MenuItem<>("Wedge D", "actionD"),
            new MenuItem<>("Wedge E", "actionE")
        );

        RadialMenu radialMenu = new RadialMenu(items, selected -> {
            centerLabel.setText(selected.label());
        });

        double innerRadius = 75.0;
        Circle centerCircle = new Circle(innerRadius);
        centerCircle.getStyleClass().add("demo-center-circle");

        StackPane hubStack = new StackPane(centerCircle, centerLabel);
        hubStack.setPickOnBounds(false);

        radialMenu.layoutXProperty().bind(canvasPane.widthProperty().divide(2));
        radialMenu.layoutYProperty().bind(canvasPane.heightProperty().divide(2));

        hubStack.layoutXProperty().bind(canvasPane.widthProperty().divide(2).subtract(innerRadius));
        hubStack.layoutYProperty().bind(canvasPane.heightProperty().divide(2).subtract(innerRadius));

        canvasPane.getChildren().addAll(radialMenu, hubStack);

        Scene scene = new Scene(canvasPane, 450, 450);
        // Load radial menu AtlantaFX integration at scene level
        scene.getStylesheets().add(getClass().getResource("/ste/fx/control/radialmenu/radial-menu-atlantafx.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/ste/fx/control/demo.css").toExternalForm());
        primaryStage.setTitle("Radial Menu Demo");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}