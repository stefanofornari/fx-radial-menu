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

import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

final class RadialMenuTestSupport {

    private RadialMenuTestSupport() {
    }

    /**
     * Fires a primary-button click on the wedge of the given item, going through
     * the same mouse handler a real click would use. Segments are the first
     * children of the menu, in item order.
     */
    static void click(RadialMenu menu, MenuItem<?> item) {
        int index = menu.items().indexOf(item);
        if (index < 0) {
            throw new IllegalArgumentException("item is not in the menu: " + item);
        }
        Node segment = menu.getChildren().get(index);
        Event.fireEvent(segment, new MouseEvent(
                MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 1,
                false, false, false, false, true, false, false, false, false, true, null));
    }
}
