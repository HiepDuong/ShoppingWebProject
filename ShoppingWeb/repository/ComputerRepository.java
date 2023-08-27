package com.shoppingweb.ShoppingWeb.repository;

import com.shoppingweb.ShoppingWeb.entity.Computer;
import com.shoppingweb.ShoppingWeb.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ComputerRepository extends JpaRepository<Computer, Long> {
    Boolean existsByModel(String model);
    @Query(value = "SELECT e FROM Computer e WHERE e.model LIKE %?1%")
    public Page<Computer> filterByModel(String keyword, Pageable pageable);
    @Query(value = "SELECT e FROM Computer e WHERE e.description LIKE %?1%")
    public Page<Computer> filterByDescription(String keyword, Pageable pageable);
    @Query(value = "SELECT e FROM Computer e WHERE e.producer LIKE %?1%")
    public Page<Computer> filterByProducer(String keyword, Pageable pageable);

    @Query(value = "SELECT a FROM Computer a JOIN a.computerProduct e WHERE e.itemId = :keyword")
    public Page<Computer> filterById(String keyword, Pageable pageable);

    @Query(value = "SELECT a FROM Computer a JOIN a.computerProduct e WHERE e.itemName LIKE %?1%")
    public Page<Computer> filterByName(String keyword, Pageable pageable);
    @Query(value = "SELECT a FROM Computer a JOIN a.computerProduct e WHERE e.category LIKE %?1%")
    public Page<Computer> filterByCategory(String keyword, Pageable pageable);
    @Query(value = "SELECT a FROM Computer a JOIN a.computerProduct e WHERE e.price > :keyword")
    public Page<Computer> filterByPriceGreaterThan(double keyword, Pageable pageable);
    @Query(value = "SELECT a FROM Computer a JOIN a.computerProduct e WHERE e.quantity > :keyword")
    public Page<Computer> filterQuantityGreaterThan(int keyword, Pageable pageable);
    @Query(value = "SELECT a FROM Computer a JOIN a.computerProduct e WHERE e.quantity < :keyword")
    public Page<Computer> filterQuantityLessThan(int keyword, Pageable pageable);
    @Query(value = "SELECT a FROM Computer a JOIN a.computerProduct e WHERE e.status = :keyword")
    public Page<Computer> filterByStatus(String keyword, Pageable pageable);
    @Query(value = "SELECT a FROM Computer a JOIN a.computerProduct e WHERE e.review > :keyword")
    public Page<Computer> filterReviewGreaterThan(double keyword, Pageable pageable);

    //Average review
    @Modifying
    @Query(value ="UPDATE product SET review = (SELECT avg(rating) FROM product_review WHERE fk_product_id = :productId) WHERE item_id = :productId", nativeQuery = true)
    public void updateReviewByAverage(Long productId);
}
