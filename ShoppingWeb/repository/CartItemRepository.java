package com.shoppingweb.ShoppingWeb.repository;

import com.shoppingweb.ShoppingWeb.entity.CartItem;
import com.shoppingweb.ShoppingWeb.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query(value = "SELECT product.price*(SELECT quantity FROM cartitem " +
            "WHERE fk_item_id = :id AND fk_cartid = :userid AND quantity = :inputquantity LIMIT 1) " +
            "FROM product WHERE product.item_id = :id", nativeQuery = true)
    public Optional<Double> productOfCartQuantityAndPrice(Long id, Long userid, int inputquantity);

    @Query(value = "SELECT * FROM cartitem WHERE fk_cartid = :userId", nativeQuery = true)
    public Page<CartItem> getAllCartItemFromCurrentUser(Long userId, Pageable pageable);
}
