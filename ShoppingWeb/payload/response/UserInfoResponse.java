package com.shoppingweb.ShoppingWeb.payload.response;

import java.util.List;

public class UserInfoResponse {
    private Long id;
    private String account;
    private String email;
    private List<String> roles;

    public UserInfoResponse(Long id, String account, String email, List<String> roles) {

        this.id = id;
        this.account = account;
        this.email = email;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public List<String> getRoles() {
        return roles;
    }

}