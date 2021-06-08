package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    public static Item createItem(Long id) {
        Item item = new Item();
        item.setId(id);
        item.setPrice(BigDecimal.valueOf(999.99));
        item.setName("Item #" + id);
        item.setDescription("Item Description #" + id);
        return item;
    }

    public static User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");
        return user;
    }

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        Item item1 = createItem(1L);
        Item item2 = createItem(2L);
        User user = createUser();
        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(item1);
        cart.addItem(item2);
        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item1));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(item2));
    }

    @Test
    public void addTocart_success() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1L);
        request.setQuantity(2);
        final ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(4, cart.getItems().size());
    }

    @Test
    public void addTocart_item_not_found() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(100L);
        request.setQuantity(2);
        final ResponseEntity<Cart> response = cartController.addTocart(request);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void removeFromcart_success() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1L);
        request.setQuantity(1);
        final ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(1, cart.getItems().size());
    }

    @Test
    public void removeFromcart_user_not_found() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("user_not_found");
        request.setItemId(1L);
        request.setQuantity(1);
        final ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(404, response.getStatusCodeValue());
    }

}
