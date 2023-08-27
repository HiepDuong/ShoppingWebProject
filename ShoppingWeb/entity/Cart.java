package com.shoppingweb.ShoppingWeb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.repository.cdi.Eager;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name ="cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cartId;

    private double total;
    private int numberOfItems;

    @OneToOne
    @JoinColumn(name = "fk_userId")
    @MapsId
    @JsonIgnore
    private AppUser cart_user;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "fk_cart_id")
    @JsonIgnore
    private List<CartItem> cartItems = new ArrayList<>();

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public AppUser getCart_user() {
        return cart_user;
    }

    public void setCart_user(AppUser cart_user) {
        this.cart_user = cart_user;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cart_cartItem) {
        this.cartItems = cart_cartItem;
    }
}
