package com.shoppingweb.ShoppingWeb.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "user")
public class AppUser {
    @Id
    @GeneratedValue(generator = "user_seq", strategy = GenerationType.SEQUENCE)
    private long userId;

    @Column(unique = true, nullable = false, length = 50)
    private String account;

    @Column(nullable = false, length = 50)
    private String fullname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String address;

    @Column(nullable = false, unique = true, length = 10)
    private String phone;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, unique = false, length = 5)
    private String sex;

    @Column(nullable = false, unique = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date birthdate;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(	name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<AppRole> roles = new HashSet<>();

    @OneToMany(mappedBy = "ratingUser", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private Set<ProductReview> user_review = new HashSet<>();

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "cart_user")
    @JsonIgnore
    private Cart cart;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "invoice_user")
    @JsonIgnore
    private List<Invoice> invoice;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @CreatedBy
    private AppUser createdBy;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;


    public AppUser() {
    }

    public AppUser(String account, String password, String fullname, String address, String phone, String email, String sex, Date birthdate) {
        this.account = account;
        this.password = password;
        this.fullname = fullname;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.sex = sex;
        this.birthdate = birthdate;
    }


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Set<AppRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<AppRole> role) {
        this.roles = role;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Set<ProductReview> getUser_review() {
        return user_review;
    }

    public void setUser_review(Set<ProductReview> user_review) {
        this.user_review = user_review;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Invoice> getInvoice() {
        return invoice;
    }

    public void setInvoice(List<Invoice> invoice) {
        this.invoice = invoice;
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


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
