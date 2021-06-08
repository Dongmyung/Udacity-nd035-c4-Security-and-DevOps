package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    public static Item createItem(Long id) {
        Item item = new Item();
        item.setId(id);
        item.setPrice(BigDecimal.valueOf(999.99));
        item.setName("Item #" + id);
        item.setDescription("Item Description #" + id);
        return item;
    }

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);

        Item item1 = createItem(1L);
        Item item2 = createItem(2L);

        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));
        when(itemRepository.findByName(item1.getName())).thenReturn(Arrays.asList(item1));
        when(itemRepository.findByName(item2.getName())).thenReturn(Arrays.asList(item2));
    }

    @Test
    public void getItems_success() {
        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(2, items.size());
    }

    @Test
    public void getItemById_success() {
        final ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Item item = response.getBody();
        assertNotNull(item);
        assertEquals("Item #1", item.getName());
    }

    @Test
    public void getItemById_item_not_found() {
        final ResponseEntity<Item> response = itemController.getItemById(500L);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getItemsByName_success() {
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("Item #2");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(1, items.size());
    }

    @Test
    public void getItemsByName_item_not_found() {
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("No Name");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
}
