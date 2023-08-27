package com.shoppingweb.ShoppingWeb.controllers;

import com.shoppingweb.ShoppingWeb.entity.AppUser;
import com.shoppingweb.ShoppingWeb.entity.Cart;
import com.shoppingweb.ShoppingWeb.entity.Invoice;
import com.shoppingweb.ShoppingWeb.exceptions.NoMatchesException;
import com.shoppingweb.ShoppingWeb.service.CartService;
import com.shoppingweb.ShoppingWeb.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserInvoiceController {
    @Autowired
    InvoiceService invoiceService;
    @PostMapping("/user/invoice")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getInvoice(@RequestBody Invoice invoice){
        return invoiceService.getInvoice(invoice);
    }

    @DeleteMapping("/user/deleteinvoice/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id){
       return invoiceService.deleteInvoice(id);
   }

    @GetMapping("/user/listinvoice/{pageNo}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Invoice>> getAllInvoice(@PathVariable(value = "pageNo") int pageNo, @Param("keyword") String keyword,
                                                       @Param("choice") String choice) throws
            SQLException{
    int pageSize = 10;
        Page<Invoice> page = invoiceService.filterWithInvoice(pageNo, pageSize, keyword, choice); //Testing sicne
        //choice cant be null
        if(page == null) throw new NoMatchesException("Wrong choice request, should be totalgreater ornumberofitemsgreater, or no invoice exist in database");
        List<Invoice> invoiceList = page.getContent();

        if (invoiceList.isEmpty()) throw new NoMatchesException("No matches, please find another keyword or choice, or no invoice existed in database");
        return new ResponseEntity<>(invoiceList, HttpStatus.OK);
    }

}
