package com.shoppingweb.ShoppingWeb.controllers;

import com.shoppingweb.ShoppingWeb.entity.AppUser;
import com.shoppingweb.ShoppingWeb.exceptions.NoMatchesException;
import com.shoppingweb.ShoppingWeb.exceptions.WrongRequestParamException;
import com.shoppingweb.ShoppingWeb.payload.request.AdminSignupRequest;
import com.shoppingweb.ShoppingWeb.payload.request.SignupRequest;
import com.shoppingweb.ShoppingWeb.service.AdminUserService;
import com.shoppingweb.ShoppingWeb.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
//@PreAuthorize from @EnableGlobalMethodSecurity(prePostEnabled = true)
//@PreAuthorize skipped ROLE_
/*
ONLY AUTHORIZE FOR ADMIN
 */
public class UserController {

    @Autowired
    AuthService authService;

    @Autowired
    AdminUserService adminUserService;


    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String userAccess() {
        return "User Content.";
    }

    @PatchMapping("/user/update/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateUser(@RequestBody SignupRequest signUpRequest,
                                         @PathVariable Long id, @RequestParam String account) {
        return adminUserService.updateUser(signUpRequest, id, account);
    }
    @DeleteMapping("/user/deleteuser/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> userDeleteOwn(@PathVariable Long id) {
        return adminUserService.UserdeleteOwn(id);
    }
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registerUser(@RequestBody AdminSignupRequest signUpRequest) {
        return authService.registerAdminService(signUpRequest);
    }

    @PatchMapping("/admin/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAdmin(@RequestBody AdminSignupRequest signUpRequest,
                                         @PathVariable Long id, @RequestParam String account) {
        return adminUserService.updateAdmin(signUpRequest, id, account);
    }

    @DeleteMapping("/admin/deleteadmin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        return adminUserService.deleteAdmin(id);
    }

    @DeleteMapping("/admin/deleteuser/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return adminUserService.deleteUser(id);
    }

    //List Of Admin
    @ResponseBody
    @GetMapping("/admin/listadmin/{pageNo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppUser>> findPaginatedAdmin(@PathVariable(value = "pageNo") int pageNo,
                                                                    @RequestParam("keyword") String keyword, @RequestParam("choice") String choice) throws
            SQLException {
        int pageSize = 10;
        Page<AppUser> page = adminUserService.getAdminWithPageFilter(pageNo, pageSize, keyword, choice); //Testing sicne
        //choice cant be null
        if(page == null) throw new NoMatchesException("Wrong choice request");
        List<AppUser> listAdmin = page.getContent();

        if (listAdmin.isEmpty()) throw new NoMatchesException("No matches, please find another keyword or choice");
        return new ResponseEntity<>(listAdmin, HttpStatus.OK);
    }

    //List Of User
    @ResponseBody
    @GetMapping("/admin/listuser/{pageNo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppUser>> findPaginatedUser(@PathVariable(value = "pageNo") int pageNo,
                                                                    @Param("keyword") String keyword, @Param("choice") String choice) throws
            SQLException {
        int pageSize = 10;
        Page<AppUser> page = adminUserService.getUserWithPageFilter(pageNo, pageSize, keyword, choice); //Testing sicne
        //choice cant be null
        if(page == null) throw new WrongRequestParamException("Wrong choice request");
        List<AppUser> listUser = page.getContent();

        if (listUser.isEmpty()) throw new NoMatchesException("No matches, please find another keyword or choice");
        return new ResponseEntity<>(listUser, HttpStatus.OK);
    }
}

