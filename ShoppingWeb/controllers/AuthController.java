package com.shoppingweb.ShoppingWeb.controllers;


import com.shoppingweb.ShoppingWeb.payload.request.LoginRequest;
import com.shoppingweb.ShoppingWeb.payload.request.SignupRequest;
import com.shoppingweb.ShoppingWeb.payload.request.TokenRefreshRequest;

import javax.validation.Valid;

import com.shoppingweb.ShoppingWeb.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
       return  authService.authenticateUserService(loginRequest);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        return authService.registerUserService(signUpRequest);
    }

    //get RefreshToken from request data
    //get RefreshToken Object(id, user, token, expiryDate) from raw Token using RefreshTokenService
    //Verify the token expired or not
    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
       return authService.refreshtokenService(request);
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutAuthenticatedaccount() {
        return authService.logoutUser();
    }
}