package com.shoppingweb.ShoppingWeb.controllers;

import com.shoppingweb.ShoppingWeb.entity.Product;
import com.shoppingweb.ShoppingWeb.exceptions.NoMatchesException;
import com.shoppingweb.ShoppingWeb.exceptions.WrongRequestParamException;
import com.shoppingweb.ShoppingWeb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {
    @Autowired
    ProductService productService;
    @GetMapping("/listproduct/{pageNo}")
    public ResponseEntity<List<Product>> findPaginatedProduct(@PathVariable(value = "pageNo") int pageNo,
                                                           @Param("keyword") String keyword, @Param("choice") String choice) throws
            SQLException {
        int pageSize = 10;
        Page<Product> page = productService.getProductWithFilter(pageNo, pageSize, keyword, choice); //Testing sicne
        //choice cant be null
        if(page == null) throw new WrongRequestParamException("Wrong choice request");
        List<Product> listProduct = page.getContent();
        if (listProduct.isEmpty()) throw new NoMatchesException("No matches, please find another keyword or choice");
        return new ResponseEntity<>(listProduct, HttpStatus.OK);
    }
}
