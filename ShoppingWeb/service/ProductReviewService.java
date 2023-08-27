package com.shoppingweb.ShoppingWeb.service;

import com.shoppingweb.ShoppingWeb.entity.*;
import com.shoppingweb.ShoppingWeb.exceptions.NoMatchesException;
import com.shoppingweb.ShoppingWeb.exceptions.WrongRequestParamException;
import com.shoppingweb.ShoppingWeb.payload.response.MessageResponse;
import com.shoppingweb.ShoppingWeb.repository.AppUserRepository;
import com.shoppingweb.ShoppingWeb.repository.ProductRepository;
import com.shoppingweb.ShoppingWeb.repository.ProductReviewRepository;
import com.shoppingweb.ShoppingWeb.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductReviewService {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductReviewRepository productReviewRepository;

    @Autowired
    AppUserRepository appUserRepository;


    //requried because of updating product.review everytime request
    public ResponseEntity<?> createReview(ProductReview productReview) {
        //Review with current user
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((UserDetailsImpl) principle).getId();
        AppUser appUser = appUserRepository.findById(userId).orElse(null);

        //SET USER to current user
        productReview.setRatingUser(appUser);

        //check if Current User has Review the Id?
        if (productReviewRepository.getDuplicateReviewItemId(productReview.getRatingProduct().getItemId(), userId) != null)
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Already give item id " + productReview.getRatingProduct().getItemId() + " review"));
        //check review 1 to 5
        if (productReview.getRating() < 0.0 || productReview.getRating() > 5.0)
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Review must be between 1.0 and 5.0"));
        //check if item exists
        if (!productRepository.existsById(productReview.getRatingProduct().getItemId())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Item id not exist"));
        }

        Product test = productRepository.findById(productReview.getRatingProduct().getItemId()).orElse(null);
        if (test == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("The Product Id that is reviewed not exist"));
        productReview.setRatingProduct(test);

        //addding to database
        ProductReview productReviewForAverage =  productReviewRepository.save(productReview);


        //MAKE SURE THE ITEM ID EXISTED


        ProductReview getProductReview = productReviewRepository.getProductReviewWithFkId(productReviewForAverage.getRatingProduct().getItemId());
        if (getProductReview != null) {
            double average = productReviewRepository.getDoubleAverage(productReview.getRatingProduct().getItemId());
            productReview.getRatingProduct().setReview(average);
        }

        return new ResponseEntity<ProductReview>(productReviewRepository.save(productReview), HttpStatus.OK);
    }

    //requried because of updating product.review everytime request
    public ResponseEntity<?> updateReview(ProductReview productReview, Long productReviewId) {

        //check if id product review id exist
        ProductReview productReviewFind = productReviewRepository.findById(productReviewId).orElse(null);
        if (productReviewFind == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Product Review id not exist"));

        //Review with current user
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = ((UserDetailsImpl) principle).getId();
        //check if product review belong to current user id
        if (productReviewFind.getRatingUser().getUserId() != userId)
            return ResponseEntity
                    .status(403)
                    .body(new MessageResponse("Current Id is " + userId + ", this product review belong to " +productReviewFind.getRatingUser().getUserId()));
        //check review 1 to 5
        if (productReview.getRating() < 0.0 || productReview.getRating() > 5.0) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Review must be between 1.0 and 5.0"));
        }

        //Check if product review ID has product id FK
        productReviewFind.getRatingUser().setUserId(userId);
        if (productReview.getRatingProduct() != null) return ResponseEntity
                .status(403)
                .body(new MessageResponse("Item Id is not belong to this product review id"));
        //NEED 3 CLICK????
        //1 not modfied: because new rating just inseted, method still use old rating data

        productReviewFind.getRatingProduct().setItemId(productReviewFind.getRatingProduct().getItemId());
        productReviewFind.setRating(productReview.getRating());
        productReviewRepository.save(productReviewFind);

        ProductReview getProductReview = productReviewRepository.getProductReviewWithFkId(productReviewFind.getRatingProduct().getItemId());
        if (getProductReview != null) {
            double average = productReviewRepository.getDoubleAverage(productReviewFind.getRatingProduct().getItemId());
            // productRepository.updateReviewByAverage(productReviewFind.getRatingProduct().getItemId());
            //  productRepository.updateReviewByAverageVariable(average, productReviewFind.getRatingProduct().getItemId());
            productReviewFind.getRatingProduct().setReview(average);
        }

        return new ResponseEntity<ProductReview>(productReviewRepository.save(productReviewFind), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteProductReview(Long id) {
        ProductReview productReview = productReviewRepository.findById(id).orElse(null);
        if (productReview == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Product Review id not exist"));

        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((UserDetailsImpl) principle).getId();
        String currentusername = ((UserDetailsImpl) principle).getUsername();

        //compare current user account and Productreview from database
        if (!userId.equals(productReview.getRatingUser().getUserId())) return ResponseEntity
                .status(403)
                .body(new MessageResponse("Your Id is " + userId + ", You can't delete from " + productReview.getRatingUser().getUserId()));
        productReviewRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Product Review : " + id + " is deleted successfully " + "from user" + userId));
    }

    public Page<ProductReview> getProductReviewWithFilter(int offset, int size, String keyword, String choice) {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((UserDetailsImpl) principle).getId();
        String currentusername = ((UserDetailsImpl) principle).getUsername();
        if (keyword != null & choice!= null) {
            if (!choice.equals("itemid") && !choice.equals("ratinggreater")) return null;
            else {
                if (choice.equals("itemid"))
                if (!keyword.matches("[0-9]+"))
                    throw new WrongRequestParamException("Request param keyword should be number only");
                else {
                    long longkeyWord = Long.valueOf(keyword);
                    return productReviewRepository.filterByItemid(longkeyWord, userId,PageRequest.of(offset - 1, size));
                }
                if (choice.equals("ratinggreater"))
                    if (!keyword.matches("[0-9]+"))
                        throw new WrongRequestParamException("Request param keyword should be number only");
                    else {
                        double doubleKeyWord = Double.valueOf(keyword);
                        if (doubleKeyWord <0.0 || doubleKeyWord > 5.0) throw new WrongRequestParamException("Input should be between 0.0 and 5.0");
                        return productReviewRepository.filterByRatingGreaterThan(doubleKeyWord, userId,PageRequest.of(offset - 1, size));
                    }
            }
        }
        return productReviewRepository.getAllProductReviewFromCurrentUser(userId, PageRequest.of(offset - 1, size));
    }
}
