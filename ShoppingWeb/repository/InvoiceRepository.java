package com.shoppingweb.ShoppingWeb.repository;

import com.shoppingweb.ShoppingWeb.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Modifying
    @Query(value = "call updateQuantityAfterInvoiced(:cartId, :itemId)", nativeQuery = true)
    void getProcedureUpdateQuantityAfterInvoiced(Long cartId, Long itemId);

    @Query(value = "SELECT * FROM spdb.invoice WHERE fk_user_id = :userid", nativeQuery = true)
    Page<Invoice> getAllInvoice(Long userid, Pageable pageable);

    @Query(value = "SELECT * FROM spdb.invoice WHERE total > :input", nativeQuery = true)
    Page<Invoice> getFilterWithTotalGreater(int input, Pageable pageable);

    @Query(value =  "SELECT * FROM spdb.invoice WHERE number_of_items > :input", nativeQuery = true)
    Page<Invoice> getFilterWithCountGreater(int input, Pageable pageable);
}
