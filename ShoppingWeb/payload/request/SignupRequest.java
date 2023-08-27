package com.shoppingweb.ShoppingWeb.payload.request;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.*;

import java.util.Date;
import java.util.Set;

public class SignupRequest {
    @NotBlank
    private String account;
    @NotBlank
    private String password;
    @NotBlank
    private String address;
    @NotBlank
    private String phone;
    @NotBlank
    private String email;
    @NotBlank
    private String fullname;
    @NotBlank
    private String sex;
    @NotBlank
    @Temporal(TemporalType.DATE)
    private Date birthdate;
    @NotBlank

    private final Set<String> role = Set.of("user");

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Set<String> getRole() {
        return this.role;
    }
}

