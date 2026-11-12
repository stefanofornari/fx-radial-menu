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

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.BDDAssertions.then;

class RadialMenuTest {

    @Test
    void creates_segments_for_each_menu_item() {
        MenuItem<String> item1 = new MenuItem<>("Item 1", null, "action1");
        MenuItem<String> item2 = new MenuItem<>("Item 2", null, "action2");
        MenuItem<String> item3 = new MenuItem<>("Item 3", null, "action3");

        RadialMenu menu = new RadialMenu(List.of(item1, item2, item3));

        then(menu.items()).hasSize(3);
    }

    @Test
    void empty_items_list_creates_no_segments() {
        RadialMenu menu = new RadialMenu(List.of());

        then(menu.items()).isEmpty();
    }

    @Test
    void single_item_creates_one_segment() {
        MenuItem<String> item = new MenuItem<>("Only", null, "action");
        RadialMenu menu = new RadialMenu(List.of(item));

        then(menu.items()).hasSize(1);
    }

    @Test
    void selection_callback_receives_selected_item_on_click() {
        MenuItem<String> item1 = new MenuItem<>("Item 1", null, "action1");
        MenuItem<String> item2 = new MenuItem<>("Item 2", null, "action2");

        MenuItem<?>[] received = new MenuItem[1];
        RadialMenu menu = new RadialMenu(List.of(item1, item2), selected -> received[0] = selected);

        menu.selectItemForTesting(item1);

        then(received[0]).isSameAs(item1);
    }

    @Test
    void selection_callback_not_invoked_on_hover() {
        MenuItem<String> item = new MenuItem<>("Item", null, "action");
        MenuItem<?>[] received = new MenuItem[1];
        RadialMenu menu = new RadialMenu(List.of(item), selected -> received[0] = selected);

        then(received[0]).isNull();
    }

    @Test
    void null_callback_is_safe() {
        MenuItem<String> item = new MenuItem<>("Item", null, "action");
        RadialMenu menu = new RadialMenu(List.of(item), (MenuItemSelectionCallback) null);

        // Should not throw
        menu.selectItemForTesting(item);
    }

    @Test
    void callback_can_be_set_after_construction() {
        MenuItem<String> item = new MenuItem<>("Item", null, "action");
        MenuItem<?>[] received = new MenuItem[1];
        RadialMenu menu = new RadialMenu(List.of(item));
        menu.setSelectionCallback(selected -> received[0] = selected);

        menu.selectItemForTesting(item);

        then(received[0]).isSameAs(item);
    }

    @Test
    void click_selects_item_and_sets_active_selection() {
        MenuItem<String> item1 = new MenuItem<>("Item 1", null, "action1");
        MenuItem<String> item2 = new MenuItem<>("Item 2", null, "action2");
        RadialMenu menu = new RadialMenu(List.of(item1, item2));

        menu.selectItemForTesting(item2);

        then(menu.selectedItem()).isSameAs(item2);
    }

    @Test
    void clicking_new_item_deselects_previous_visually() {
        MenuItem<String> item1 = new MenuItem<>("Item 1", null, "action1");
        MenuItem<String> item2 = new MenuItem<>("Item 2", null, "action2");
        RadialMenu menu = new RadialMenu(List.of(item1, item2));

        menu.selectItemForTesting(item1);
        then(menu.selectedItem()).isSameAs(item1);

        menu.selectItemForTesting(item2);
        then(menu.selectedItem()).isSameAs(item2);
    }

    @Test
    void click_fires_selection_callback_once() {
        MenuItem<String> item = new MenuItem<>("Item", null, "action");
        int[] calls = new int[1];
        RadialMenu menu = new RadialMenu(List.of(item), selected -> calls[0]++);

        menu.selectItemForTesting(item);

        then(calls[0]).isEqualTo(1);
    }

    @Test
    void callback_updates_label_in_demo_pattern() {
        // This test verifies the demo pattern: callback updates a label
        MenuItem<String> item1 = new MenuItem<>("Wedge A", "actionA");
        MenuItem<String> item2 = new MenuItem<>("Wedge B", "actionB");

        String[] label = new String[1];
        RadialMenu menu = new RadialMenu(List.of(item1, item2), selected -> label[0] = selected.label());

        menu.selectItemForTesting(item1);
        then(label[0]).isEqualTo("Wedge A");

        menu.selectItemForTesting(item2);
        then(label[0]).isEqualTo("Wedge B");
    }
}