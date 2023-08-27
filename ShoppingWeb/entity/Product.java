package com.shoppingweb.ShoppingWeb.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.util.*;

@Entity
@Table
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "item_id")
    private Long itemId;

    @Column(nullable = false, unique = true, length = 50)
    private String itemName;
    @Column(nullable = false, length =50)
    private String category;

    @Column(nullable = false, length = 50)
    private double price;

    @Column(nullable = false)
    private int quantity;
    @Column
    private boolean status;

    @Column(precision = 1,scale = 2)
    //precision: maximum DIgit
    //scale maximum after .decimal
    private double review;
    final private Date dateAdded = new Date();
    private String image;

    private String fileType;

    @OneToMany(mappedBy = "ratingProduct", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private List<ProductReview> product_review = new ArrayList<>();

    @OneToMany(mappedBy = "cartItem_product", cascade = CascadeType.REMOVE)
    private List<CartItem> product_cartItem = new ArrayList<>();
    //@OneToOne Book mapped By

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public double getReview() {
        return review;
    }

    public void setReview(double review) {
        this.review = review;
    }
    public Date getDateAdded() {
        return dateAdded;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}
