package com.shoppingweb.ShoppingWeb.controllers;

import com.shoppingweb.ShoppingWeb.entity.Product;
import com.shoppingweb.ShoppingWeb.entity.ProductReview;
import com.shoppingweb.ShoppingWeb.exceptions.NoMatchesException;
import com.shoppingweb.ShoppingWeb.exceptions.WrongRequestParamException;
import com.shoppingweb.ShoppingWeb.payload.request.AdminSignupRequest;
import com.shoppingweb.ShoppingWeb.service.AdminUserService;
import com.shoppingweb.ShoppingWeb.service.AuthService;
import com.shoppingweb.ShoppingWeb.service.ProductReviewService;
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
//@PreAuthorize from @EnableGlobalMethodSecurity(prePostEnabled = true)

/*
ONLY AUTHORIZE FOR User
 */
public class UserReviewController {
    @Autowired
    ProductReviewService productReviewService;
    @Autowired
    AdminUserService adminUserService;

    @PostMapping("/user/addproductreview")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> addProductReview(@RequestBody ProductReview productReview) {

        return productReviewService.createReview(productReview);
    }

    @PatchMapping("/user/updateproductreview/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateProductReview(@RequestBody ProductReview productReview, @PathVariable Long id) {
        return productReviewService.updateReview(productReview, id);
    }

    @DeleteMapping("/user/deleteproductreview/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteProductReview(@PathVariable Long id) {
        return productReviewService.deleteProductReview(id);
    }

    @GetMapping("/user/listproductreview/{pageNo}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ProductReview>> findPaginatedProductReview(@PathVariable(value = "pageNo") int pageNo,
                                                              @Param("keyword") String keyword, @Param("choice") String choice) throws
            SQLException {
        int pageSize = 10;
        Page<ProductReview> page = productReviewService.getProductReviewWithFilter(pageNo, pageSize, keyword, choice); //Testing sicne
        //choice cant be null
        if(page == null) throw new WrongRequestParamException("Wrong choice request");
        List<ProductReview> productReviewList = page.getContent();
        if (productReviewList.isEmpty()) throw new NoMatchesException("No matches, please find another keyword or choice");
        return new ResponseEntity<>(productReviewList, HttpStatus.OK);
    }
}
