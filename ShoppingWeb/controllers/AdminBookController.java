package com.shoppingweb.ShoppingWeb.controllers;

import com.shoppingweb.ShoppingWeb.entity.Book;
import com.shoppingweb.ShoppingWeb.entity.Computer;
import com.shoppingweb.ShoppingWeb.exceptions.NoMatchesException;
import com.shoppingweb.ShoppingWeb.exceptions.WrongRequestParamException;
import com.shoppingweb.ShoppingWeb.service.BookService;
import com.shoppingweb.ShoppingWeb.service.ComputerService;
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
public class AdminBookController {

    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";
    @Autowired
    BookService bookService;
    @PostMapping("/admin/addbook")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addBook(@ModelAttribute Book book, @RequestParam("image")MultipartFile file) throws IOException {
        return bookService.createBook(book, file);
    }
    @PatchMapping("/admin/updatebook/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @ModelAttribute Book book, @RequestParam("image")MultipartFile file) throws IOException {
        return bookService.updateBook(id, book, file);
    }
    @DeleteMapping("/admin/deletebook/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteComputer(@PathVariable Long id) {
        return bookService.deleteBook(id);
    }
    @GetMapping("/listbook/{pageNo}")
    public ResponseEntity<List<Book>> findPaginatedBook(@PathVariable(value = "pageNo") int pageNo,
                                                               @Param("keyword") String keyword, @Param("choice") String choice) throws
            SQLException {
        int pageSize = 10;
        Page<Book> page = bookService.getBookWithFilter(pageNo, pageSize, keyword, choice); //Testing sicne
        //choice cant be null
        if(page == null) throw new WrongRequestParamException("Wrong 'choice' param request");
        List<Book> listBook = page.getContent();
        if (listBook.isEmpty()) throw new NoMatchesException("No matches, please find another keyword or choice");
        return new ResponseEntity<>(listBook, HttpStatus.OK);
    }
    @GetMapping("/singlebook/{id}")
    public ResponseEntity<Book> findSingleBook(@PathVariable Long id) {
        return bookService.getSingleBook(id);
    }
}