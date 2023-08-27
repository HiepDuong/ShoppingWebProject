package com.shoppingweb.ShoppingWeb.service;

import com.shoppingweb.ShoppingWeb.entity.AppRole;
import com.shoppingweb.ShoppingWeb.entity.AppUser;
import com.shoppingweb.ShoppingWeb.entity.ERole;
import com.shoppingweb.ShoppingWeb.exceptions.AccessDeniedException;
import com.shoppingweb.ShoppingWeb.exceptions.NoMatchesException;
import com.shoppingweb.ShoppingWeb.exceptions.UnauthorizedActionException;
import com.shoppingweb.ShoppingWeb.payload.request.AdminSignupRequest;
import com.shoppingweb.ShoppingWeb.payload.request.SignupRequest;
import com.shoppingweb.ShoppingWeb.payload.response.MessageResponse;
import com.shoppingweb.ShoppingWeb.repository.AppUserRepository;
import com.shoppingweb.ShoppingWeb.repository.RefreshTokenRepository;
import com.shoppingweb.ShoppingWeb.repository.RoleRepository;
import com.shoppingweb.ShoppingWeb.security.jwt.JwtUtils;
import com.shoppingweb.ShoppingWeb.security.services.RefreshTokenService;
import com.shoppingweb.ShoppingWeb.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class AdminUserService {
    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    RefreshTokenService refreshTokenService;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    PasswordEncoder encoder;

    public ResponseEntity<?> updateAdmin(AdminSignupRequest signUpRequest, Long id, String account) {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((UserDetailsImpl) principle).getId();
        String currentusername = ((UserDetailsImpl) principle).getUsername();

        if (!userId.equals(id)) return ResponseEntity
                .status(403)
                .body(new MessageResponse("Id is " + userId + ", You can't access this resource"));

        if (!currentusername.equals(account)) return ResponseEntity
                .status(403)
                .body(new MessageResponse("Account is: " + currentusername + ", You can't access this resource"));

        AppUser appUser = appUserRepository.findById(id).orElse(null);


        //Check Syntax name Error
        if (signUpRequest.getAccount() != null) {
            if (appUserRepository.existsByAccount(signUpRequest.getAccount())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Username is already taken!"));
            }
            appUser.setAccount(signUpRequest.getAccount());
        }
        if (signUpRequest.getFullname() != null) {
            if (signUpRequest.getFullname() != null) {
                if (signUpRequest.getFullname().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: name contain non alphabet "));
                appUser.setFullname(signUpRequest.getFullname());
            }
        }
        if (signUpRequest.getPassword() != null) {
            if (signUpRequest.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)" + ".+$") == false)
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Password must have lowercase, uppercase, and number "));

            if (signUpRequest.getPassword().length() < 6) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Password must have at least 6 characters "));

            appUser.setPassword(encoder.encode(signUpRequest.getPassword()));
        }
        if (signUpRequest.getAddress() != null) appUser.setAddress(signUpRequest.getAddress());
        if (signUpRequest.getPhone() != null) {
            if (appUserRepository.existsByPhone(signUpRequest.getPhone()))
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Phone is already in use!"));
            if (signUpRequest.getPhone().length() != 10) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Phone must be 10 digit numbers with no white space"));
            if (signUpRequest.getPhone().matches("[0-9]+") == false) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Phone can only contain numbers"));

            appUser.setPhone(signUpRequest.getPhone());

        }
        if (signUpRequest.getEmail() != null) {
            if (signUpRequest.getEmail().endsWith("@gmail.com") == false) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email must be gmail.com "));

            appUser.setEmail(signUpRequest.getEmail());
        }
        if (signUpRequest.getSex() != null) appUser.setSex(signUpRequest.getSex());
        if (signUpRequest.getBirthdate() != null) {
            if (signUpRequest.getBirthdate().after(new Date())) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Birthday must be before " + new Date()));

            Calendar year18fromnowCalendar = Calendar.getInstance();
            Date dateFromNow = new Date();
            year18fromnowCalendar.setTime(dateFromNow);
            year18fromnowCalendar.add(Calendar.YEAR, -18);
            Date year18past = year18fromnowCalendar.getTime();
            //must be at least 18 yo
            if (signUpRequest.getBirthdate().after(year18past)) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Birthday must be before " + new Date()));
            //cant be older than 60
            year18fromnowCalendar.add(Calendar.YEAR, -42);
            Date year60past = year18fromnowCalendar.getTime();
            if (signUpRequest.getBirthdate().before(year60past)) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You are older than 60, you can't register "));
            appUser.setBirthdate(signUpRequest.getBirthdate());
        }

        return new ResponseEntity<AppUser>(appUserRepository.save(appUser), HttpStatus.OK);

    }
    public ResponseEntity<?> updateUser(SignupRequest signUpRequest, Long id, String account) {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((UserDetailsImpl) principle).getId();
        String currentusername = ((UserDetailsImpl) principle).getUsername();

        if (!userId.equals(id)) return ResponseEntity
                .status(403)
                .body(new MessageResponse("Your Id is " + userId + ", You can't access this resource"));

        if (!currentusername.equals(account)) return ResponseEntity
                .status(403)
                .body(new MessageResponse("Account is: " + currentusername + ", You can't access this resource"));

        AppUser appUser = appUserRepository.findById(id).orElse(null);


        //Check Syntax name Error
        if (signUpRequest.getAccount() != null) {
            if (appUserRepository.existsByAccount(signUpRequest.getAccount())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Username is already taken!"));
            }
            appUser.setAccount(signUpRequest.getAccount());
        }
        if (signUpRequest.getFullname() != null) {
            if (signUpRequest.getFullname() != null) {
                if (signUpRequest.getFullname().matches("^[ A-Za-z]+$") == false) return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: name contain non alphabet "));
                appUser.setFullname(signUpRequest.getFullname());
            }
        }
        if (signUpRequest.getPassword() != null) {
            if (signUpRequest.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)" + ".+$") == false)
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Password must have lowercase, uppercase, and number "));

            if (signUpRequest.getPassword().length() < 6) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Password must have at least 6 characters "));

            appUser.setPassword(encoder.encode(signUpRequest.getPassword()));
        }
        if (signUpRequest.getAddress() != null) appUser.setAddress(signUpRequest.getAddress());
        if (signUpRequest.getPhone() != null) {
            if (appUserRepository.existsByPhone(signUpRequest.getPhone()))
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Phone is already in use!"));
            if (signUpRequest.getPhone().length() != 10) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Phone must be 10 digit numbers with no white space"));
            if (signUpRequest.getPhone().matches("[0-9]+") == false) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Phone can only contain numbers"));

            appUser.setPhone(signUpRequest.getPhone());

        }
        if (signUpRequest.getEmail() != null) {
            if (signUpRequest.getEmail().endsWith("@gmail.com") == false) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email must be gmail.com "));

            appUser.setEmail(signUpRequest.getEmail());
        }
        if (signUpRequest.getSex() != null) appUser.setSex(signUpRequest.getSex());
        if (signUpRequest.getBirthdate() != null) {
            if (signUpRequest.getBirthdate().after(new Date())) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Birthday must be before " + new Date()));

            Calendar year18fromnowCalendar = Calendar.getInstance();
            Date dateFromNow = new Date();
            year18fromnowCalendar.setTime(dateFromNow);
            year18fromnowCalendar.add(Calendar.YEAR, -18);
            Date year18past = year18fromnowCalendar.getTime();
            //must be at least 18 yo
            if (signUpRequest.getBirthdate().after(year18past)) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Birthday must be before " + new Date()));
            //cant be older than 60
            year18fromnowCalendar.add(Calendar.YEAR, -42);
            Date year60past = year18fromnowCalendar.getTime();
            if (signUpRequest.getBirthdate().before(year60past)) return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: You are older than 60, you can't register "));
            appUser.setBirthdate(signUpRequest.getBirthdate());
        }

        return new ResponseEntity<AppUser>(appUserRepository.save(appUser), HttpStatus.OK);

    }
    public ResponseEntity<?> deleteAdmin(Long id) {

        AppUser user = appUserRepository.findById(id).orElse(null);
        if (user == null) throw new NoMatchesException("Id is not exist");

        if (user.getRoles().stream().findFirst().orElse(null).getId() != 2) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("This is not an admin account"));

        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principle.toString() != "anonymousUser") {
            Long userId = ((UserDetailsImpl) principle).getId();
            refreshTokenService.deleteByUserId(userId);
        }

        /*
        setting Cookie , Refresh toKen to null
        so can log out and then delete the user that has refresh token
         */
        ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        appUserRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Admin: " + id + " is deleted successfully!"));
    }
    public ResponseEntity<?> deleteUser(Long id) {

        AppUser user = appUserRepository.findById(id).orElse(null);
        if (user == null) throw new NoMatchesException("User Id does not exist");

        if (user.getRoles().stream().findFirst().orElse(null).getId() != 1) throw new UnauthorizedActionException("This is not a User account!");

        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principle.toString() != "anonymousUser") {
            Long userId = ((UserDetailsImpl) principle).getId();
            refreshTokenService.deleteByUserId(userId);
        }

        /*
        setting Cookie , Refresh toKen to null
        so can log out and then delete the user that has refresh token
         */
        ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        appUserRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("User: " + id + " is deleted successfully!"));
    }
    public ResponseEntity<?> UserdeleteOwn(Long id) {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = ((UserDetailsImpl) principle).getId();
        AppUser user = appUserRepository.findById(id).orElse(null);

        if (user == null) throw new NoMatchesException("User Id does not exist");

        if(userId != id ) throw new UnauthorizedActionException("Your id is " + userId +"you can't delete other user account");

        if (principle.toString() != "anonymousUser") {
            refreshTokenService.deleteByUserId(userId);
        }
        /*
        setting Cookie , Refresh toKen to null
        so can log out and then delete the user that has refresh token
         */
        ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        appUserRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("User: " + id + " is deleted successfully!"));
    }
    public Page<AppUser> getAdminWithPageFilter(int offset, int size, String keyword, String choice) {
        if (keyword != null & choice!= null) {
            if (!choice.equals("fullname") && !choice.equals("id") && !choice.equals("phone")
                    && !choice.equals("email")
                    && !choice.equals("sex")) return null;
            else {
                if (choice.equals("fullname"))
                    return appUserRepository.filterByAdminName(keyword, PageRequest.of(offset - 1, size));

                else if (choice.equals("id")) {
                    if (keyword.isEmpty()) return appUserRepository.findAll(PageRequest.of(offset - 1, size));
                    long longKeyword = Long.valueOf(keyword);
                    return appUserRepository.filterByAdminId(keyword, PageRequest.of(offset - 1, size));
                } else if (choice.equals("phone"))
                    return appUserRepository.filterByAdminPhone(keyword, PageRequest.of(offset - 1, size));
                else if (choice.equals("email"))
                    return appUserRepository.filterByAdminEmail(keyword, PageRequest.of(offset - 1, size));
                else if (choice.equals("sex"))
                    return appUserRepository.filterByAdminSex(keyword, PageRequest.of(offset - 1, size));
            }
        }
        return appUserRepository.filterByAdminOnly(PageRequest.of(offset - 1, size));
    }
    public Page<AppUser> getUserWithPageFilter(int offset, int size, String keyword, String choice) {
        if (keyword != null) {
            if (!choice.equals("fullname") && !choice.equals("id") && !choice.equals("phone")
                    && !choice.equals("email")
                    && !choice.equals("sex")) return null;
            else {
                if (choice.equals("fullname"))
                    return appUserRepository.filterByUserName(keyword, PageRequest.of(offset - 1, size));

                else if (choice.equals("id")) {
                    if (keyword.isEmpty()) return appUserRepository.findAll(PageRequest.of(offset - 1, size));
                    long longKeyword = Long.valueOf(keyword);
                    return appUserRepository.filterByUserId(keyword, PageRequest.of(offset - 1, size));
                } else if (choice.equals("phone"))
                    return appUserRepository.filterByUserPhone(keyword, PageRequest.of(offset - 1, size));
                else if (choice.equals("email"))
                    return appUserRepository.filterByUserEmail(keyword, PageRequest.of(offset - 1, size));
                else if (choice.equals("sex"))
                    return appUserRepository.filterByUserSex(keyword, PageRequest.of(offset - 1, size));
            }
        }
        return appUserRepository.filterByUserOnly(PageRequest.of(offset - 1, size));
    }
}
