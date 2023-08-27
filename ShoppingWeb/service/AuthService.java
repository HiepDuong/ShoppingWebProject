package com.shoppingweb.ShoppingWeb.service;

import com.shoppingweb.ShoppingWeb.entity.AppRole;
import com.shoppingweb.ShoppingWeb.entity.AppUser;
import com.shoppingweb.ShoppingWeb.entity.ERole;
import com.shoppingweb.ShoppingWeb.entity.RefreshToken;
import com.shoppingweb.ShoppingWeb.exceptions.RoleNotFoundInDatabaseException;
import com.shoppingweb.ShoppingWeb.exceptions.TokenRefreshException;
import com.shoppingweb.ShoppingWeb.payload.request.AdminSignupRequest;
import com.shoppingweb.ShoppingWeb.payload.request.LoginRequest;
import com.shoppingweb.ShoppingWeb.payload.request.SignupRequest;
import com.shoppingweb.ShoppingWeb.payload.request.TokenRefreshRequest;
import com.shoppingweb.ShoppingWeb.payload.response.UserInfoResponse;
import com.shoppingweb.ShoppingWeb.payload.response.MessageResponse;
import com.shoppingweb.ShoppingWeb.payload.response.TokenRefreshResponse;
import com.shoppingweb.ShoppingWeb.repository.AppUserRepository;
import com.shoppingweb.ShoppingWeb.repository.RoleRepository;
import com.shoppingweb.ShoppingWeb.security.jwt.JwtUtils;
import com.shoppingweb.ShoppingWeb.security.services.RefreshTokenService;
import com.shoppingweb.ShoppingWeb.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class AuthService {

    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AppUserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;


    public ResponseEntity<?> authenticateUserService(LoginRequest loginRequest) {

        /*Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getAccount(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(), userDetails.getId(),
                userDetails.getUsername(), userDetails.getEmail(), roles));*/
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getAccount(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new UserInfoResponse(userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles));

    }

    public ResponseEntity<?> registerUserService(SignupRequest signUpRequest) {
        //Check for null
        if (signUpRequest.getAccount().isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Account is null!"));
        if (signUpRequest.getFullname().isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Fullname is null!"));
        if (signUpRequest.getPassword().isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Password is null!"));
        if (signUpRequest.getAddress().isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Address is null!"));
        if (signUpRequest.getPhone().isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Phone is null!"));
        if (signUpRequest.getEmail().isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Email is null!"));
        if (signUpRequest.getBirthdate().toString().isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Birthdate is null!"));

        //check unique constraint
        if (userRepository.existsByAccount(signUpRequest.getAccount())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        if (userRepository.existsByPhone(signUpRequest.getPhone())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Phone is already in use!"));
        }
        //Check Syntax name Error
        if (signUpRequest.getFullname().matches("^[ A-Za-z]+$") == false)  return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: name contain non alphabet "));

        if (signUpRequest.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)" +".+$") == false) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Password must have lowercase, uppercase, and number "));

        if (signUpRequest.getPassword().length()<6) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Password must have at least 6 characters "));

        if (signUpRequest.getPhone().length()!=10) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Phone must be 10 digit numbers with no white space"));
        if (signUpRequest.getPhone().matches("[0-9]+")==false )return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Phone can only contain numbers"));

        if (signUpRequest.getEmail().endsWith("@gmail.com") == false) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Email must be gmail.com "));

        if(signUpRequest.getBirthdate().after(new Date()) )return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Birthday must be before " + new Date()));

        Calendar year18fromnowCalendar = Calendar.getInstance();
        Date dateFromNow = new Date();
        year18fromnowCalendar.setTime(dateFromNow);
        year18fromnowCalendar.add(Calendar.YEAR, -18);
        Date year18past = year18fromnowCalendar.getTime();
        //must be at least 18 yo
        if(signUpRequest.getBirthdate().after(year18past)) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Birthday must be before " + new Date()));
        //cant be older than 60
        year18fromnowCalendar.add(Calendar.YEAR, -42);
        Date year60past = year18fromnowCalendar.getTime();
        if(signUpRequest.getBirthdate().before(year60past))return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: You are older than 60, you can't register "));

        // Create new user's account
        AppUser user = new AppUser(signUpRequest.getAccount(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFullname(),
                signUpRequest.getAddress(),
                signUpRequest.getPhone(),
                signUpRequest.getEmail(),
                signUpRequest.getSex(),
                signUpRequest.getBirthdate());

        Set<String> strRoles = signUpRequest.getRole();
        Set<AppRole> roles = new HashSet<>();

        if (strRoles == null) {
            AppRole userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RoleNotFoundInDatabaseException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        AppRole adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RoleNotFoundInDatabaseException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        AppRole userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RoleNotFoundInDatabaseException("Error: Role is not found in database."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    public ResponseEntity<?> registerAdminService(AdminSignupRequest signUpRequest) {
        //Check for null
        if (signUpRequest.getAccount().isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Account is null!"));
        if (signUpRequest.getPassword().isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Password is null!"));
        if (signUpRequest.getFullname().isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Fullname is null!"));
        if (signUpRequest.getAddress().isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Address is null!"));
        if (signUpRequest.getPhone().isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Phone is null!"));
        if (signUpRequest.getEmail().isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Email is null!"));
        if (signUpRequest.getBirthdate().toString().isEmpty()) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Birthdate is null!"));

        //check unique constraint
        if (userRepository.existsByAccount(signUpRequest.getAccount())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        if (userRepository.existsByPhone(signUpRequest.getPhone())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Phone is already in use!"));
        }

        //Check Syntax name Error

        if (signUpRequest.getFullname().matches("^[ A-Za-z]+$") == false)  return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: name contain non alphabet "));

        if (signUpRequest.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)" +".+$") == false) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Password must have lowercase, uppercase, and number "));

        if (signUpRequest.getPassword().length()<6) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Password must have at least 6 characters "));

        if (signUpRequest.getPhone().length()!=10) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Phone must be 10 digit numbers with no white space"));
        if (signUpRequest.getPhone().matches("[0-9]+")==false )return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Phone can only contain numbers"));

        if (signUpRequest.getEmail().endsWith("@gmail.com") == false) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Email must be gmail.com "));

        if(signUpRequest.getBirthdate().after(new Date()) )return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Birthday must be before " + new Date()));

        Calendar year18fromnowCalendar = Calendar.getInstance();
        Date dateFromNow = new Date();
        year18fromnowCalendar.setTime(dateFromNow);
        year18fromnowCalendar.add(Calendar.YEAR, -18);
        Date year18past = year18fromnowCalendar.getTime();
        //must be at least 18 yo
        if(signUpRequest.getBirthdate().after(year18past)) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: Birthday must be before " + new Date()));
        //cant be older than 60
        year18fromnowCalendar.add(Calendar.YEAR, -42);
        Date year60past = year18fromnowCalendar.getTime();
        if(signUpRequest.getBirthdate().before(year60past))return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: You are older than 60, you can't register "));


        // Create new user's account
        AppUser user = new AppUser(signUpRequest.getAccount(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getFullname(),
                signUpRequest.getAddress(),
                signUpRequest.getPhone(),
                signUpRequest.getEmail(),
                signUpRequest.getSex(),
                signUpRequest.getBirthdate());

        Set<String> strRoles = signUpRequest.getRole();
        Set<AppRole> roles = new HashSet<>();

            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        AppRole adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;

                    default:
                        AppRole userRole = roleRepository.findByName(ERole.ROLE)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Admin registered successfully!"));
    }

    public ResponseEntity<?> logoutUser() {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principle.toString() != "anonymousUser") {
            Long userId = ((UserDetailsImpl) principle).getId();
            refreshTokenService.deleteByUserId(userId);
        }

        ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new MessageResponse("You've been signed out!"));
    }
    public ResponseEntity<?> refreshtokenService(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)    //Verify the token expired or not
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getAccount()); //generate new access token
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));//return if done
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }
}
