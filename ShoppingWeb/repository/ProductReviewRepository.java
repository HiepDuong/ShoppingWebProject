package com.shoppingweb.ShoppingWeb.repository;

import com.shoppingweb.ShoppingWeb.entity.Computer;
import com.shoppingweb.ShoppingWeb.entity.Product;
import com.shoppingweb.ShoppingWeb.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    @Query(value = "SELECT * FROM product_review WHERE fk_product_id = :productId AND fk_user_id = :userId LIMIT 1", nativeQuery = true)
    public ProductReview getDuplicateReviewItemId(Long productId, Long userId);

    @Query(value ="SELECT * FROM product_review WHERE fk_product_id = :productId LIMIT 1", nativeQuery = true)
    public ProductReview getProductReviewWithFkId(Long productId);

    @Query(value ="SELECT * FROM product_review WHERE fk_product_id = :productId AND fk_user_id = :userId", nativeQuery = true)
    public Page<ProductReview> filterByItemid(Long productId, long userId,Pageable pageable);
    @Query(value ="SELECT * FROM product_review WHERE rating > :compareRating AND fk_user_id = :userId ", nativeQuery = true)
    public Page<ProductReview> filterByRatingGreaterThan(double compareRating, long userId, Pageable pageable);
    @Query(value = "SELECT * FROM product_review WHERE  fk_user_id = :userId", nativeQuery = true)
    public Page<ProductReview> getAllProductReviewFromCurrentUser(Long userId, Pageable pageable);

    @Query(value= "SELECT avg(rating) FROM product_review WHERE fk_product_id = :productid", nativeQuery = true)
    public double getDoubleAverage(Long productid);

}
