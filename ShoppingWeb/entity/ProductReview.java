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
@Table
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(precision = 1,scale = 2)
    //precision: maximum DIgit
    //scale maximum after .decimal
    private double rating;
    @ManyToOne
    @JoinColumn(name = "fk_user_id")
    private AppUser ratingUser;

    @ManyToOne
    @JoinColumn(name = "fk_product_id")
    private Product ratingProduct;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public AppUser getRatingUser() {
        return ratingUser;
    }

    public void setRatingUser(AppUser ratingUser) {
        this.ratingUser = ratingUser;
    }

    public Product getRatingProduct() {
        return ratingProduct;
    }

    public void setRatingProduct(Product ratingProduct) {
        this.ratingProduct = ratingProduct;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
