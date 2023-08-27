package com.shoppingweb.ShoppingWeb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name ="cartitem")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long cartItemId;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double subTotal;

    @ManyToOne
    @JoinColumn(name ="fk_itemId")
    private Product cartItem_product;

    @ManyToOne
    @JoinColumn(name ="fk_cartid")
    private Cart fk_cart_id;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @CreatedBy
    private AppUser createdBy;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    @LastModifiedBy
    private AppUser updatedBy;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Long getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Long cartItemId) {
        this.cartItemId = cartItemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public Product getCartItem_product() {
        return cartItem_product;
    }

    public void setCartItem_product(Product cartItem_product) {
        this.cartItem_product = cartItem_product;
    }

    public Cart getFk_cart_id() {
        return fk_cart_id;
    }

    public void setFk_cart_id(Cart fk_cart_id) {
        this.fk_cart_id = fk_cart_id;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public AppUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(AppUser createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public AppUser getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(AppUser updatedBy) {
        this.updatedBy = updatedBy;
    }


}
