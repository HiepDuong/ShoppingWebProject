package com.shoppingweb.ShoppingWeb.repository;

import com.shoppingweb.ShoppingWeb.entity.AppUser;
import com.shoppingweb.ShoppingWeb.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    //Optional<Product> findById(Long id);
    Boolean existsByItemName(String itemName);


    @Query(value = "SELECT e FROM Product e WHERE e.itemId = :keyword")
    public Page<Product> filterById(String keyword, Pageable pageable);
    @Query(value = "SELECT e FROM Product e WHERE e.itemName LIKE %?1%")
    public Page<Product> filterByName(String keyword, Pageable pageable);
    @Query(value = "SELECT e FROM Product e WHERE e.category LIKE %?1%")
    public Page<Product> filterByCategory(String keyword, Pageable pageable);
    @Query(value = "SELECT e FROM Product e WHERE e.price > :keyword")
    public Page<Product> filterByPriceGreaterThan(double keyword, Pageable pageable);
    @Query(value = "SELECT e FROM Product e WHERE e.quantity > :keyword")
    public Page<Product> filterQuantityGreaterThan(int keyword, Pageable pageable);
    @Query(value = "SELECT e FROM Product e WHERE e.quantity < :keyword")
    public Page<Product> filterQuantityLessThan(int keyword, Pageable pageable);
    @Query(value = "SELECT e FROM Product e WHERE e.status = :keyword")
    public Page<Product> filterByStatus(String keyword, Pageable pageable);
    @Query(value = "SELECT e FROM Product e WHERE e.review > :keyword")
    public Page<Product> filterReviewGreaterThan(double keyword, Pageable pageable);

    @Modifying
    @Query(value ="UPDATE product SET review = (SELECT avg(rating) FROM product_review WHERE fk_product_id = :productId) WHERE item_id = :productId", nativeQuery = true)
    public void updateReviewByAverage(Long productId);

    @Modifying
    @Query(value ="UPDATE product SET review = :average WHERE item_id = :productId", nativeQuery = true)
    public void updateReviewByAverageVariable(double average, Long productId);

}
