package com.shoppingweb.ShoppingWeb.service;

import com.shoppingweb.ShoppingWeb.entity.*;
import com.shoppingweb.ShoppingWeb.exceptions.WrongRequestParamException;
import com.shoppingweb.ShoppingWeb.payload.response.MessageResponse;
import com.shoppingweb.ShoppingWeb.repository.AppUserRepository;
import com.shoppingweb.ShoppingWeb.repository.CartItemRepository;
import com.shoppingweb.ShoppingWeb.repository.CartRepository;
import com.shoppingweb.ShoppingWeb.repository.ProductRepository;
import com.shoppingweb.ShoppingWeb.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CartRepository cartRepository;

    @Autowired
    AppUserRepository appUserRepository;
    public ResponseEntity<?> addCartItem(CartItem cartItem) {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((UserDetailsImpl) principle).getId();
        AppUser appUser = appUserRepository.findById(userId).orElse(null);
        Cart existed = cartRepository.findById(userId).orElse(null);


        //IF cart is already inserted, just update
        if(existed!=null){
            double total = cartRepository.getTotalFromAllCartItem(userId).orElse(0.0);
            int count = cartRepository.getNumberOfOrder(userId).orElse(0);
            existed.setTotal(total);
            existed.setNumberOfItems(count);
            existed.setCart_user(appUser);
            cartItem.setFk_cart_id(cartRepository.save(existed));
        }
        else {
            //if no cart, create new one
            Cart cart = new Cart();
            double total = cartRepository.getTotalFromAllCartItem(userId).orElse(0.0);
            int count = cartRepository.getNumberOfOrder(userId).orElse(0);
            cart.setTotal(total);
            cart.setNumberOfItems(count);
            cart.setCart_user(appUser);
            cartItem.setFk_cart_id(cartRepository.save(cart));
        }
        //check if id exists of product
        Product product = productRepository.findById(cartItem.getCartItem_product().getItemId()).orElse(null);
        if (product == null)
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Item id not exist"));
        //after checking id, checking condition
        if (cartItem.getQuantity() > product.getQuantity()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: cart requested quantity is more than available quantity"));

        if (product.isStatus() == false) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: current item is not available, status available is " + product.isStatus()));

        if (cartItem.getQuantity() < 0)
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Quantity need to be > 0"));

        //make sure Sub Total does change
        cartItem.setCartItem_product(product);
        double productOfQuantityAndPrice1 = cartItemRepository.productOfCartQuantityAndPrice(cartItem.getCartItem_product().getItemId(),
                userId, cartItem.getQuantity()).orElse(0.0);

        cartItem.setSubTotal(productOfQuantityAndPrice1);

        //in database first (CartItem get in database)
        cartItemRepository.save(cartItem);

        Cart existed2 = cartRepository.findById(userId).orElse(null);

        //using query form MYSQL getting Sub Total
        //save initial sub total or else 1st entry will be 0
        double productOfQuantityAndPrice = cartItemRepository.productOfCartQuantityAndPrice(cartItem.getCartItem_product().getItemId(),
                userId, cartItem.getQuantity()).orElse(0.0);
        cartItem.setSubTotal(productOfQuantityAndPrice);

        cartItemRepository.save(cartItem);
        double total = cartRepository.getTotalFromAllCartItem(userId).orElse(0.0);
        existed2.setTotal(total);

        int count = cartRepository.getNumberOfOrder(userId).orElse(0);
        existed2.setNumberOfItems(count);
        cartItem.setFk_cart_id(existed2);

        return new ResponseEntity<CartItem>(cartItemRepository.save(cartItem), HttpStatus.OK);
    }

    public ResponseEntity<?> updateCartItem(Long id, CartItem cartItem) {
        CartItem cartItem2 = cartItemRepository.findById(id).orElse(null);
        if (cartItem2 == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Cart Item id not exist"));

        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = ((UserDetailsImpl) principle).getId();
        String currentusername = ((UserDetailsImpl) principle).getUsername();
        if (cartItem2.getFk_cart_id().getCartId() != userId)
            return ResponseEntity
                    .status(403)
                    .body(new MessageResponse("Current Id is " + userId + ", this Cart Item id doesn't belong to you"));

        if (cartItem.getQuantity() < 0)
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Quantity need to be > 0"));
        Cart existed1 = cartRepository.findById(userId).orElse(null);
        cartItem2.setQuantity(cartItem.getQuantity());
        //in database first
        cartItemRepository.save(cartItem2);

        cartItem2.getCartItem_product();
        double productOfQuantityAndPrice1 = cartItemRepository.productOfCartQuantityAndPrice(cartItem2.getCartItem_product().getItemId(),
                userId, cartItem.getQuantity()).orElse(0.0);
        cartItem2.setSubTotal(productOfQuantityAndPrice1);

        //in database first (CartItem get in database)
        cartItemRepository.save(cartItem2);

        Cart existed2 = cartRepository.findById(userId).orElse(null);

        //using query form MYSQL getting Sub Total
        //save initial sub total or else 1st entry will be 0
        double productOfQuantityAndPrice = cartItemRepository.productOfCartQuantityAndPrice(cartItem2.getCartItem_product().getItemId(),
                userId, cartItem.getQuantity()).orElse(0.0);
        cartItem2.setSubTotal(productOfQuantityAndPrice);

        cartItemRepository.save(cartItem2);
        double total = cartRepository.getTotalFromAllCartItem(userId).orElse(0.0);
        existed2.setTotal(total);

        int count = cartRepository.getNumberOfOrder(userId).orElse(0);
        existed2.setNumberOfItems(count);
        cartItem.setFk_cart_id(existed2);

        return new ResponseEntity<CartItem>(cartItemRepository.save(cartItem2), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteCartId(Long id) {
        CartItem cartItem = cartItemRepository.findById(id).orElse(null);
        if (cartItem == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Cart Item id not exist"));

        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((UserDetailsImpl) principle).getId();
        String currentusername = ((UserDetailsImpl) principle).getUsername();

        //compare current user account and Productreview from database
        if (!userId.equals(cartItem.getFk_cart_id().getCartId())) return ResponseEntity
                .status(403)
                .body(new MessageResponse("Your Id is " + userId + ", You can't delete from " + cartItem.getFk_cart_id().getCartId()));
        cartItemRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Cart item : " + id + " is deleted successfully " + "from user" + userId));
    }
    public Page<CartItem> getCartItemList(int offset, int size) {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((UserDetailsImpl) principle).getId();

        return cartItemRepository.getAllCartItemFromCurrentUser(userId, PageRequest.of(offset - 1, size));
    }

}
