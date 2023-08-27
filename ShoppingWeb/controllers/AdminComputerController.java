package com.shoppingweb.ShoppingWeb.controllers;

import com.shoppingweb.ShoppingWeb.entity.Book;
import com.shoppingweb.ShoppingWeb.entity.Computer;
import com.shoppingweb.ShoppingWeb.entity.Product;
import com.shoppingweb.ShoppingWeb.exceptions.NoMatchesException;
import com.shoppingweb.ShoppingWeb.exceptions.WrongRequestParamException;
import com.shoppingweb.ShoppingWeb.service.ComputerService;
import com.shoppingweb.ShoppingWeb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminComputerController {
    @Autowired
    ComputerService computerService;
    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";
    @PostMapping("/admin/addcomputer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addComputer(@ModelAttribute Computer computer, @RequestParam("image") MultipartFile file) throws IOException {

        return computerService.createComputer(computer, file);
    }
    @PatchMapping("/admin/updatecomputer/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateComputer(@PathVariable Long id, @ModelAttribute Computer computer, @RequestParam("image") MultipartFile file)
            throws IOException {

        return computerService.updateComputer(id, computer, file);
    }
    @DeleteMapping("/admin/deletecomputer/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteComputer(@PathVariable Long id) {
        return computerService.deleteComputer(id);
    }
    @GetMapping("/listcomputer/{pageNo}")
    public ResponseEntity<List<Computer>> findPaginatedProduct(@PathVariable(value = "pageNo") int pageNo,
                                                              @Param("keyword") String keyword, @Param("choice") String choice) throws
            SQLException {
        int pageSize = 10;
        Page<Computer> page = computerService.getComputerWithFilter(pageNo, pageSize, keyword, choice); //Testing sicne
        //choice cant be null
        if(page == null) throw new WrongRequestParamException("Wrong 'choice' param request");
        List<Computer> listComputer = page.getContent();
        if (listComputer.isEmpty()) throw new NoMatchesException("No matches, please find another keyword or choice");
        return new ResponseEntity<>(listComputer, HttpStatus.OK);
    }
    @GetMapping("/singlecomputer/{id}")
    public ResponseEntity<Computer> findSingleBook(@PathVariable Long id) {
        return computerService.getSingleComputer(id);
    }
}