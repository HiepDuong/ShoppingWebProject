package com.shoppingweb.ShoppingWeb.service;

import com.shoppingweb.ShoppingWeb.entity.*;
import com.shoppingweb.ShoppingWeb.payload.response.MessageResponse;

import com.shoppingweb.ShoppingWeb.repository.AppUserRepository;
import com.shoppingweb.ShoppingWeb.repository.CartRepository;
import com.shoppingweb.ShoppingWeb.repository.ProductRepository;
import com.shoppingweb.ShoppingWeb.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CartService {
    @Autowired
    CartRepository cartRepository;
    @Autowired
    ProductRepository productRepository;

    @Autowired
    AppUserRepository appUserRepository;

    public ResponseEntity<Cart> getCart() {

        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((UserDetailsImpl) principle).getId();

       Cart existed = cartRepository.findById(userId).orElse(null);
        //IF cart is already inserted, just update
        if(existed!=null){
            double total = cartRepository.getTotalFromAllCartItem(userId).orElse(0.0);
            int count = cartRepository.getNumberOfOrder(userId).orElse(0);
            AppUser appUser = appUserRepository.findById(userId).orElse(null);
            existed.setTotal(total);
            existed.setNumberOfItems(count);
            existed.setCart_user(appUser);
            return new ResponseEntity<Cart>(cartRepository.save(existed), HttpStatus.OK);
        }
        //if no cart, create new one
        AppUser appUser = appUserRepository.findById(userId).orElse(null);

        Cart cart = new Cart();

        double total = cartRepository.getTotalFromAllCartItem(userId).orElse(0.0);
        int count = cartRepository.getNumberOfOrder(userId).orElse(0);
        cart.setTotal(total);
        cart.setNumberOfItems(count);
        cart.setCart_user(appUser);

        return new ResponseEntity<Cart>(cartRepository.save(cart), HttpStatus.OK);
    }
    public ResponseEntity<?> deleteCart(Long id) {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = ((UserDetailsImpl) principle).getId();

        Cart cart = cartRepository.findById(id).orElse(null);
        if (cart == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: No Cart Id existed or wrong id, your correct is "+ userId));
        if (cart.getCartId() != userId) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: This Cart Id is not your, your id is "+ userId));

        cartRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Cart Id : " + id + " is deleted successfully"));
    }
}
