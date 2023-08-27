package com.shoppingweb.ShoppingWeb.repository;

import com.shoppingweb.ShoppingWeb.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query(value = "SELECT count(*) from cartitem WHERE fk_cartid = :userid", nativeQuery = true)
    public Optional<Integer> getNumberOfOrder(Long userid);

    @Query(value = "SELECT SUM(sub_total) FROM cartitem where fk_cartid = :userid", nativeQuery = true)
    public Optional<Double> getTotalFromAllCartItem(Long userid);
}
