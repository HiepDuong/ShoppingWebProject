package com.shoppingweb.ShoppingWeb.repository;

import com.shoppingweb.ShoppingWeb.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = "SELECT e FROM Book e WHERE e.author LIKE %?1%")
    public Page<Book> filterByAuthor(String keyword, Pageable pageable);
    @Query(value = "SELECT e FROM Book e WHERE e.genre LIKE %?1%")
    public Page<Book> filterByGenre(String keyword, Pageable pageable);
    @Query(value = "SELECT e FROM Book e WHERE e.publisher LIKE %?1%")
    public Page<Book> filterByPublisher(String keyword, Pageable pageable);

    @Query(value = "SELECT a FROM Book a JOIN a.bookProduct e WHERE e.itemName LIKE %?1%")
    public Page<Book> filterByName(String keyword, Pageable pageable);
    @Query(value = "SELECT a FROM Book a JOIN a.bookProduct e WHERE e.category LIKE %?1%")
    public Page<Book> filterByCategory(String keyword, Pageable pageable);
    @Query(value = "SELECT a FROM Book a JOIN a.bookProduct e WHERE e.price > :keyword")
    public Page<Book> filterByPriceGreaterThan(double keyword, Pageable pageable);
    @Query(value = "SELECT a FROM Book a JOIN a.bookProduct e WHERE e.quantity > :keyword")
    public Page<Book> filterQuantityGreaterThan(int keyword, Pageable pageable);
    @Query(value = "SELECT a FROM Book a JOIN a.bookProduct e WHERE e.quantity < :keyword")
    public Page<Book> filterQuantityLessThan(int keyword, Pageable pageable);
    @Query(value = "SELECT a FROM Book a JOIN a.bookProduct e WHERE e.status = :keyword")
    public Page<Book> filterByStatus(String keyword, Pageable pageable);
    @Query(value = "SELECT a FROM Book a JOIN a.bookProduct e WHERE e.review > :keyword")
    public Page<Book> filterReviewGreaterThan(double keyword, Pageable pageable);

    @Modifying
    @Query(value ="UPDATE product SET review = (SELECT avg(rating) FROM product_review WHERE fk_product_id = :productId) WHERE item_id = :productId", nativeQuery = true)
    public void updateReviewByAverage(Long productId);
}
