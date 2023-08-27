package com.shoppingweb.ShoppingWeb.controllers;

import com.shoppingweb.ShoppingWeb.entity.Cart;
import com.shoppingweb.ShoppingWeb.entity.CartItem;
import com.shoppingweb.ShoppingWeb.exceptions.NoMatchesException;
import com.shoppingweb.ShoppingWeb.service.CartService;
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
public class CartController {
    @Autowired
    CartService cartService;
    @GetMapping("/user/cart")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Cart> getCart(){
        return cartService.getCart();
    }

    @DeleteMapping("/user/deletecart/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteCart(@PathVariable Long id){
        return cartService.deleteCart(id);
    }
}
