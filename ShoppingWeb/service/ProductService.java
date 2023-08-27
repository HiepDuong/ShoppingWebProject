package com.shoppingweb.ShoppingWeb.service;

import com.shoppingweb.ShoppingWeb.entity.Product;
import com.shoppingweb.ShoppingWeb.entity.Product;
import com.shoppingweb.ShoppingWeb.exceptions.NoMatchesException;
import com.shoppingweb.ShoppingWeb.exceptions.WrongRequestParamException;
import com.shoppingweb.ShoppingWeb.payload.response.MessageResponse;
import com.shoppingweb.ShoppingWeb.repository.BookRepository;
import com.shoppingweb.ShoppingWeb.repository.ComputerRepository;
import com.shoppingweb.ShoppingWeb.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    ProductRepository repo;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    ComputerRepository computerRepository;

    public Page<Product> getProductWithFilter(int offset, int size, String keyword, String choice) {
        if (keyword != null& choice!= null) {
            if (!choice.equals("itemname") && !choice.equals("category") && !choice.equals("price")
                    && !choice.equals("status")
                    && !choice.equals("quantitygreater")
                    && !choice.equals("quantityless")
                    && !choice.equals("reviewgreater")
                    && !choice.equals("reviewless")) return null;
            else {
                if (choice.equals("itemname"))
                    return repo.filterByName(keyword, PageRequest.of(offset - 1, size));

                else if (choice.equals("category")) {
                    if (keyword.isEmpty()) return repo.findAll(PageRequest.of(offset - 1, size));
                    return repo.filterByCategory(keyword, PageRequest.of(offset - 1, size));
                } else if (choice.equals("price")) {

                    if (!keyword.matches("[0-9]+"))
                        throw new WrongRequestParamException("Request param keyword should be number only");
                    else {
                        double intkeyword = Double.valueOf(keyword);
                        return repo.filterByPriceGreaterThan(intkeyword, PageRequest.of(offset - 1, size));
                    }
                } else if (choice.equals("status")) {
                    if (!keyword.equals("1") || !keyword.equals("0") || !keyword.equals("true") || !keyword.equals("false"))
                        throw new WrongRequestParamException("Request param keyword should be 0,1 or false,true");
                    else return repo.filterByStatus(keyword, PageRequest.of(offset - 1, size));
                } else if (choice.equals("quantitygreater")) {
                    if (!keyword.matches("[0-9]+"))
                        throw new WrongRequestParamException("Request param keyword should be number only");
                    else {
                        int intkeyword = Integer.valueOf(keyword);
                        return repo.filterQuantityGreaterThan(intkeyword, PageRequest.of(offset - 1, size));
                    }
                } else if (choice.equals("quantityless")) {
                    if (!keyword.matches("[0-9]+"))
                        throw new WrongRequestParamException("Request param keyword should be number only");
                    else {
                        int intkeyword = Integer.valueOf(keyword);
                        return repo.filterQuantityLessThan(intkeyword, PageRequest.of(offset - 1, size));
                    }
                } else if (choice.equals("reviewgreater")) {
                    if (!keyword.matches("[0-9]+"))
                        throw new WrongRequestParamException("Request param keyword should be number only");
                    else {
                        double intkeyword = Double.valueOf(keyword);
                        return repo.filterReviewGreaterThan(intkeyword, PageRequest.of(offset - 1, size));
                    }
                }
            }
        }
        return repo.findAll(PageRequest.of(offset - 1, size));
    }
}
