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

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RadialMenuTest {

    @Test
    void createsSegmentsForEachMenuItem() {
        MenuItem<String> item1 = new MenuItem<>("Item 1", null, "action1");
        MenuItem<String> item2 = new MenuItem<>("Item 2", null, "action2");
        MenuItem<String> item3 = new MenuItem<>("Item 3", null, "action3");

        RadialMenu menu = new RadialMenu(List.of(item1, item2, item3));

        assertEquals(3, menu.items().size(), "Should create one item per menu item");
    }

    @Test
    void emptyItemsListCreatesNoSegments() {
        RadialMenu menu = new RadialMenu(List.of());
        assertTrue(menu.items().isEmpty(), "Empty items list should produce no items");
    }

    @Test
    void singleItemCreatesOneSegment() {
        MenuItem<String> item = new MenuItem<>("Only", null, "action");
        RadialMenu menu = new RadialMenu(List.of(item));
        assertEquals(1, menu.items().size());
    }

    @Test
    void selectionCallbackReceivesSelectedItemOnClick() {
        MenuItem<String> item1 = new MenuItem<>("Item 1", null, "action1");
        MenuItem<String> item2 = new MenuItem<>("Item 2", null, "action2");

        MenuItem<?>[] received = new MenuItem[1];
        RadialMenu menu = new RadialMenu(List.of(item1, item2), selected -> received[0] = selected);

        menu.selectItemForTesting(item1);
        assertSame(item1, received[0], "Callback should receive the clicked item");
    }

    @Test
    void selectionCallbackNotInvokedOnHover() {
        MenuItem<String> item = new MenuItem<>("Item", null, "action");
        MenuItem<?>[] received = new MenuItem[1];
        RadialMenu menu = new RadialMenu(List.of(item), selected -> received[0] = selected);

        assertNull(received[0], "Callback should not be invoked without click");
    }

    @Test
    void nullCallbackIsSafe() {
        MenuItem<String> item = new MenuItem<>("Item", null, "action");
        RadialMenu menu = new RadialMenu(List.of(item), (MenuItemSelectionCallback) null);

        // Should not throw
        menu.selectItemForTesting(item);
    }

    @Test
    void callbackCanBeSetAfterConstruction() {
        MenuItem<String> item = new MenuItem<>("Item", null, "action");
        MenuItem<?>[] received = new MenuItem[1];
        RadialMenu menu = new RadialMenu(List.of(item));
        menu.setSelectionCallback(selected -> received[0] = selected);

        menu.selectItemForTesting(item);
        assertSame(item, received[0]);
    }

    @Test
    void clickSelectsItemAndSetsActiveSelection() {
        MenuItem<String> item1 = new MenuItem<>("Item 1", null, "action1");
        MenuItem<String> item2 = new MenuItem<>("Item 2", null, "action2");
        RadialMenu menu = new RadialMenu(List.of(item1, item2));

        menu.selectItemForTesting(item2);
        assertSame(item2, menu.selectedItem(), "Clicked item should become active selection");
    }

    @Test
    void clickingNewItemDeselectsPreviousVisually() {
        MenuItem<String> item1 = new MenuItem<>("Item 1", null, "action1");
        MenuItem<String> item2 = new MenuItem<>("Item 2", null, "action2");
        RadialMenu menu = new RadialMenu(List.of(item1, item2));

        menu.selectItemForTesting(item1);
        assertSame(item1, menu.selectedItem());

        menu.selectItemForTesting(item2);
        assertSame(item2, menu.selectedItem());
    }

    @Test
    void clickFiresSelectionCallbackOnce() {
        MenuItem<String> item = new MenuItem<>("Item", null, "action");
        int[] calls = new int[1];
        RadialMenu menu = new RadialMenu(List.of(item), selected -> calls[0]++);

        menu.selectItemForTesting(item);
        assertEquals(1, calls[0], "Selection callback should fire once per selection");
    }
}