package com.shoppingweb.ShoppingWeb.exceptions;

public class WrongRequestParamException extends RuntimeException{
    public WrongRequestParamException(String message){
        super(message);}
}
