package com.shoppingweb.ShoppingWeb.service;

import com.shoppingweb.ShoppingWeb.entity.Book;
import com.shoppingweb.ShoppingWeb.entity.Book;
import com.shoppingweb.ShoppingWeb.exceptions.NoMatchesException;
import com.shoppingweb.ShoppingWeb.exceptions.WrongRequestParamException;
import com.shoppingweb.ShoppingWeb.payload.response.MessageResponse;
import com.shoppingweb.ShoppingWeb.repository.BookRepository;
import com.shoppingweb.ShoppingWeb.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
    @Autowired
    BookRepository repo;
    @Autowired
    ProductRepository productRepository;
    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";
    public ResponseEntity<?> createBook(Book book, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        if (!fileType.matches("image/png")) return ResponseEntity.badRequest().body("File not image.png!");
        String filePath = Paths.get(uploadDirectory, fileName).toString();
        book.getBookProduct().setImage(filePath);
        book.getBookProduct().setFileType(fileType);

        //save to Upload SpringBoot local
        Files.copy(file.getInputStream(),Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        book.getBookProduct().setReview(0.0); //product before created always 0.0
        if (productRepository.existsByItemName(book.getBookProduct().getItemName())) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Item name is existed in database "));
        if (book.getBookProduct().getItemName().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: name contain non alphabet "));
        if (book.getBookProduct().getCategory().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Category contain non alphabet "));
        if (book.getAuthor().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Author contain non alphabet "));
        if (book.getGenre().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Genre contain non alphabet "));
        if (book.getPublisher().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Publisher contain non alphabet "));
        return new ResponseEntity<Book>(repo.save(book), HttpStatus.OK);
    }

    public ResponseEntity<?> updateBook(Long id, Book update, MultipartFile file) throws IOException {
        Book book = repo.findById(id).orElseThrow(() -> new NoMatchesException("Book does not exist"));

        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String fileType = file.getContentType();
            String filePath = Paths.get(uploadDirectory, fileName).toString();
            if (!fileType.matches("image/png")) return ResponseEntity.badRequest().body("File not image.png!");

            book.getBookProduct().setImage(filePath);
            book.getBookProduct().setFileType(fileType);
            System.out.println(file.getOriginalFilename());
            //save to Upload SpringBoot local
            Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        }

        if (update.getAuthor() != null) {
            if (update.getAuthor().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Producer contain non alphabet "));
            book.setAuthor(update.getAuthor());
        }
        if (update.getGenre() != null) {
            if (update.getGenre().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Genre contain non alphabet "));
            book.setGenre(update.getGenre());
        }

        if (update.getPublisher() != null) {
            if (update.getPublisher().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Publisher contain non alphabet "));
            book.setPublisher(update.getPublisher());
        }

        if (update.getBookProduct().getItemName() != null){
            if (update.getBookProduct().getItemName().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: name contain non alphabet "));
            book.getBookProduct().setItemName(update.getBookProduct().getItemName());
        }
        if (update.getBookProduct().getCategory() != null){
            if (update.getBookProduct().getCategory().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Category contain non alphabet "));
            book.getBookProduct().setCategory(update.getBookProduct().getCategory());
        }
        if (update.getBookProduct().getPrice() != 0.0)
            book.getBookProduct().setPrice(update.getBookProduct().getPrice());
        if (update.getBookProduct().getQuantity() != 0)
            book.getBookProduct().setQuantity(update.getBookProduct().getQuantity());
        if (update.getBookProduct().isStatus() != false)
            book.getBookProduct().setStatus(update.getBookProduct().isStatus());
        if (update.getBookProduct().getReview() != 0.0)
            book.getBookProduct().setReview(update.getBookProduct().getReview());//Using average query
        return new ResponseEntity<Book>(repo.save(book), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteBook(Long id) {
        Book Book = repo.findById(id).orElseThrow(() -> new NoMatchesException("Book does not exist"));
        repo.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Book: " + id + " is deleted successfully!"));
    }

    public Page<Book> getBookWithFilter(int offset, int size, String keyword, String choice) {
        if (keyword != null & choice!= null) {
            if (!choice.equals("author") && !choice.equals("genre") && !choice.equals("publisher")
                    && !choice.equals("itemname") && !choice.equals("category") && !choice.equals("price")
                    && !choice.equals("status")
                    && !choice.equals("quantitygreater")
                    && !choice.equals("quantityless")
                    && !choice.equals("reviewgreater")
                    && !choice.equals("reviewless")) return null;
            else {
                if (choice.equals("author"))
                    return repo.filterByAuthor(keyword, PageRequest.of(offset - 1, size));

                else if (choice.equals("genre")) {
                    return repo.filterByGenre(keyword, PageRequest.of(offset - 1, size));
                } else if (choice.equals("publisher")) {
                    if (!keyword.matches("[0-9]+"))
                        throw new WrongRequestParamException("Request param keyword should be number only");
                    else {
                        return repo.filterByPublisher(keyword, PageRequest.of(offset - 1, size));
                    }
                }
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

   // @Transactional
    public ResponseEntity<Book> getSingleBook(Long id) {
        Book book = repo.findById(id).orElseThrow(() -> new NoMatchesException("Book does not exist"));
        //repo.updateReviewByAverage(id);
        return new ResponseEntity<Book>(book, HttpStatus.OK);
    }
}
