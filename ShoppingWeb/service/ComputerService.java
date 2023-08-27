package com.shoppingweb.ShoppingWeb.service;

import com.shoppingweb.ShoppingWeb.entity.Book;
import com.shoppingweb.ShoppingWeb.entity.Computer;
import com.shoppingweb.ShoppingWeb.entity.Product;
import com.shoppingweb.ShoppingWeb.exceptions.NoMatchesException;
import com.shoppingweb.ShoppingWeb.exceptions.WrongRequestParamException;
import com.shoppingweb.ShoppingWeb.payload.response.MessageResponse;
import com.shoppingweb.ShoppingWeb.repository.ComputerRepository;
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

@Service
public class ComputerService {

    @Autowired
    ProductService productService;
    @Autowired
    ComputerRepository repo;

    @Autowired
    ProductRepository productRepository;
    public static String uploadDirectory = System.getProperty("user.dir") + "/uploads";
    public ResponseEntity<?> createComputer(Computer computer, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileType = file.getContentType();
        String filePath = Paths.get(uploadDirectory, fileName).toString();
        if (!fileType.matches("image/png")) return ResponseEntity.badRequest().body("File not image.png!");

        computer.getComputerProduct().setImage(filePath);
        computer.getComputerProduct().setFileType(fileType);
        System.out.println(file.getOriginalFilename());
        //save to Upload SpringBoot local
        Files.copy(file.getInputStream(),Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        if (repo.existsByModel(computer.getModel()))return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Model already existed in database "));
        if (computer.getProducer().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Producer contain non alphabet "));
        computer.getComputerProduct().setReview(0.0); //product before created always 0.0
        if (productRepository.existsByItemName(computer.getComputerProduct().getItemName())) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Item name is existed in database "));
        if (computer.getComputerProduct().getItemName().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: name contain non alphabet "));
        if (computer.getComputerProduct().getCategory().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Category contain non alphabet "));

        return new ResponseEntity<Computer>(repo.save(computer), HttpStatus.OK);
    }

    public ResponseEntity<?> updateComputer(Long id, Computer update, MultipartFile file) throws IOException {

        Computer computer = repo.findById(id).orElseThrow(() -> new NoMatchesException("Computer does not exist"));

        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String fileType = file.getContentType();
            String filePath = Paths.get(uploadDirectory, fileName).toString();
            if (!fileType.matches("image/png")) return ResponseEntity.badRequest().body("File not image.png!");

            computer.getComputerProduct().setImage(filePath);
            computer.getComputerProduct().setFileType(fileType);
            System.out.println(file.getOriginalFilename());
            //save to Upload SpringBoot local
            Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        }

        if (update.getProducer() != null) {
            if (update.getProducer().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Producer contain non alphabet "));
             computer.setProducer(update.getProducer());
        }
        if (update.getModel() != null) computer.setModel(update.getModel());
        if (update.getDescription() != null) computer.setDescription(update.getDescription());


        if (update.getComputerProduct().getItemName() != null) {
            if (update.getComputerProduct().getItemName().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: name contain non alphabet "));
            computer.getComputerProduct().setItemName(update.getComputerProduct().getItemName());
        }
        if (update.getComputerProduct().getCategory() != null) {
            if (update.getComputerProduct().getCategory().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Category contain non alphabet "));
            computer.getComputerProduct().setCategory(update.getComputerProduct().getCategory());
        }
        if (update.getComputerProduct().getPrice() != 0.0)
            computer.getComputerProduct().setPrice(update.getComputerProduct().getPrice());
        if (update.getComputerProduct().getQuantity() != 0)
            computer.getComputerProduct().setQuantity(update.getComputerProduct().getQuantity());
        if (update.getComputerProduct().isStatus() != false)
            computer.getComputerProduct().setStatus(update.getComputerProduct().isStatus());
        if (update.getComputerProduct().getReview() != 0.0)
            computer.getComputerProduct().setReview(update.getComputerProduct().getReview());//Using average query
        return new ResponseEntity<Computer>(repo.save(computer), HttpStatus.OK);
    }

    public ResponseEntity<?> deleteComputer(Long id) {
        Computer computer = repo.findById(id).orElseThrow(() -> new NoMatchesException("Computer does not exist"));
        repo.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Computer: " + id + " is deleted successfully!"));
    }

    public Page<Computer> getComputerWithFilter(int offset, int size, String keyword, String choice) {
        if (keyword != null & choice!= null) {
            if (!choice.equals("model") && !choice.equals("description") && !choice.equals("producer")
                    && !choice.equals("itemname") && !choice.equals("category") && !choice.equals("price")
                    && !choice.equals("status")
                    && !choice.equals("quantitygreater")
                    && !choice.equals("quantityless")
                    && !choice.equals("reviewgreater")
                    && !choice.equals("reviewless")) return null;
            else {
                if (choice.equals("model"))
                    return repo.filterByModel(keyword, PageRequest.of(offset - 1, size));

                else if (choice.equals("description")) {
                    return repo.filterByDescription(keyword, PageRequest.of(offset - 1, size));
                } else if (choice.equals("producer")) {
                    if (!keyword.matches("[0-9]+"))
                        throw new WrongRequestParamException("Request param keyword should be number only");
                    else {
                        return repo.filterByProducer(keyword, PageRequest.of(offset - 1, size));
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

    @Transactional
    public ResponseEntity<Computer> getSingleComputer(Long id) {
        Computer computer = repo.findById(id).orElseThrow(() -> new NoMatchesException("Computer id does not exist"));
        //WHAT HAPPENED IF the updateReview by average has no one updated yet?
        //repo.updateReviewByAverage(id);
        return new ResponseEntity<Computer>(computer, HttpStatus.OK);
    }
}