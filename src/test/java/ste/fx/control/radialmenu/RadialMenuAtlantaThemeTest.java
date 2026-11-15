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

import javafx.application.Application;
import javafx.application.Platform;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

class RadialMenuAtlantaThemeTest {

    @BeforeAll
    static void startToolkit() {
        try {
            Platform.startup(() -> {
            });
        } catch (IllegalStateException alreadyRunning) {
        }
    }

    private static RadialMenu menuWithFourItems() {
        return new RadialMenu(List.of(
                new MenuItem<>("Scope", "a"), new MenuItem<>("Story", "b"),
                new MenuItem<>("Code", "c"), new MenuItem<>("Explore", "d")));
    }

    @Test
    void atlantafx_theme_keeps_hover_and_selected_states() {
        String previous = Application.getUserAgentStylesheet();
        try {
            Application.setUserAgentStylesheet(
                    new atlantafx.base.theme.PrimerLight().getUserAgentStylesheet());

            RadialMenu menu = menuWithFourItems();
            Pane root = new Pane(menu);
            Scene scene = new Scene(root, 500, 500);
            scene.getStylesheets().add(getClass().getResource(
                    "/ste/fx/control/radialmenu/radial-menu-atlantafx.css").toExternalForm());
            root.applyCss();
            root.layout();

            Path segment = (Path) menu.getChildren().get(0);
            Text label = menu.labels().get(0);
            Color base = (Color) segment.getFill();
            then(base).isNotNull();

            segment.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
            menu.applyCss();
            then((Color) segment.getFill()).isNotEqualTo(base);

            menu.selectItemForTesting(menu.items().get(0));
            segment.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), false);
            menu.applyCss();
            then((Color) segment.getFill()).isNotEqualTo(base);
            then(label.getFill()).isNotEqualTo(Color.BLACK);
        } finally {
            Application.setUserAgentStylesheet(previous);
        }
    }
}
