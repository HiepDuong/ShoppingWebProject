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
@Table(name ="invoiceItem")
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long invoiceItemid;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double subTotal;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name ="fk_itemId")
    private Product invoiceItem_product;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name ="fk_invoiceId")
    private Invoice invoiceItem_invoice;


    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Long getInvoiceItemid() {
        return invoiceItemid;
    }

    public void setInvoiceItemid(Long invoiceItemid) {
        this.invoiceItemid = invoiceItemid;
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

    public Product getInvoiceItem_product() {
        return invoiceItem_product;
    }

    public void setInvoiceItem_product(Product invoiceItem_product) {
        this.invoiceItem_product = invoiceItem_product;
    }

    public Invoice getInvoiceItem_invoice() {
        return invoiceItem_invoice;
    }

    public void setInvoiceItem_invoice(Invoice invoiceItem_invoice) {
        this.invoiceItem_invoice = invoiceItem_invoice;
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
