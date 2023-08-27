package com.shoppingweb.ShoppingWeb.controllers;

import com.shoppingweb.ShoppingWeb.entity.CartItem;
import com.shoppingweb.ShoppingWeb.exceptions.NoMatchesException;
import com.shoppingweb.ShoppingWeb.service.AdminUserService;
import com.shoppingweb.ShoppingWeb.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CartItemController {
    @Autowired
    CartItemService cartItemService;
    @Autowired
    AdminUserService adminUserService;

    @PostMapping("/user/addcartitem")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addProductReview(@RequestBody CartItem cartItem) {
        return cartItemService.addCartItem(cartItem);
    }
    @PatchMapping("/user/updatecartitem/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateCartItem(@RequestBody CartItem cartItem, @PathVariable Long id) {
        return cartItemService.updateCartItem(id, cartItem);
    }

    @DeleteMapping("/user/deletecartitem/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long id) {
        return cartItemService.deleteCartId(id);
    }

    @GetMapping("/user/listcartitem/{pageNo}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CartItem>> findPaginatedCartIem(@PathVariable(value = "pageNo") int pageNo) throws
            SQLException {
        int pageSize = 10;
        Page<CartItem> page = cartItemService.getCartItemList(pageNo, pageSize); //Testing sicne
        List<CartItem> cartItems = page.getContent();
        if (cartItems.isEmpty()) throw new NoMatchesException("No matches, no cart existed in database");
        return new ResponseEntity<>(cartItems, HttpStatus.OK);
    }
}