package com.shoppingweb.ShoppingWeb.service;

import com.shoppingweb.ShoppingWeb.entity.*;
import com.shoppingweb.ShoppingWeb.exceptions.NoMatchesException;
import com.shoppingweb.ShoppingWeb.exceptions.UnauthorizedActionException;
import com.shoppingweb.ShoppingWeb.exceptions.WrongRequestParamException;
import com.shoppingweb.ShoppingWeb.payload.response.MessageResponse;
import com.shoppingweb.ShoppingWeb.repository.*;
import com.shoppingweb.ShoppingWeb.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.List;

@Service
public class InvoiceService {
    @Autowired
    InvoiceRepository invoiceRepository;

    @Autowired
    InvoiceItemRepository invoiceItemRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    ProductRepository productRepository;
    @Transactional(rollbackFor = Exception.class)

    //roll back if exception happened => not save(invoice) line 61
    public ResponseEntity<?> getInvoice(Invoice invoice) {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((UserDetailsImpl) principle).getId();
        Cart checkCart = cartRepository.findById(userId).orElse(null);
        //check if cart is avaialble in current account
        if (checkCart == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: No Cart available, please added Cart Item "));

        //Get Information for Invoice
        Invoice saveInvoice = new Invoice();

        //getting inputted shipping address
        saveInvoice.setShippingAddress(invoice.getShippingAddress());

        //Setting current user id
        AppUser appUser = appUserRepository.findById(userId).orElse(null);
        saveInvoice.setInvoice_user(appUser);

        //
        saveInvoice.setNumberOfItems(checkCart.getNumberOfItems());
        saveInvoice.setTotal(checkCart.getTotal());

        //save Invoice => create new Invoice
        //From this getting its invoice id
        saveInvoice = invoiceRepository.save(saveInvoice);

        //Getting Invoice Item from Cart Item
        List<CartItem>  cartItemList = checkCart.getCartItems();
        List<InvoiceItem> copiedListInvoiceItem= new ArrayList<>();
        for (int i = 0; i < cartItemList.size(); i++) {
            CartItem copiedCartItem = cartItemList.get(i);
            InvoiceItem copiedInvoiceItem = new InvoiceItem();


            Product product = productRepository.findById(copiedCartItem.getCartItem_product().getItemId()).orElse(null);
            //checking condition, before updating quantity after getting invoice
            if (copiedCartItem.getQuantity() > product.getQuantity()) throw
                    new UnauthorizedActionException("Error: Invoice item with product id "
                    + copiedCartItem.getCartItem_product().getItemId()+ " quantity is more than available quantity");

            if (product.isStatus() == false) throw new
                    UnauthorizedActionException("Error: Invoice item with product id" + product.getItemId() +  "status available is " + product.isStatus());

            copiedInvoiceItem.setQuantity(copiedCartItem.getQuantity());
            copiedInvoiceItem.setSubTotal(copiedCartItem.getSubTotal());
            copiedInvoiceItem.setInvoiceItem_product(copiedCartItem.getCartItem_product());
            copiedInvoiceItem.setInvoiceItem_invoice(saveInvoice);
            saveInvoice.setInvoiceItemList(copiedListInvoiceItem);
            saveInvoice.getInvoiceItemList().add(copiedInvoiceItem);

            product.setQuantity(product.getQuantity()-copiedCartItem.getQuantity());

            //Update Cart No delete, opt for delete BY ID in line 98
          //  invoiceRepository.getProcedureUpdateQuantityAfterInvoiced(copiedCartItem.getCartItemId(),copiedCartItem.getCartItem_product().getItemId());


            invoiceItemRepository.save(copiedInvoiceItem);
        }
        //reset the cart
        cartRepository.deleteById(userId);
        return new ResponseEntity<Invoice>(invoiceRepository.save(saveInvoice), HttpStatus.OK);
    }

    public Page<Invoice> filterWithInvoice(int offset, int size, String keyword, String choice){
        //make sure to belong to current user
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((UserDetailsImpl) principle).getId();
        if (keyword != null & choice!= null) {
            if (!choice.equals("totalgreater") && !choice.equals("numberofitemsgreater")) return null;
            else {
                if (choice.equals("totalgreater")) {
                    if (!keyword.matches("[0-9]+"))
                        throw new WrongRequestParamException("Request param keyword should be integer only");
                    else {
                        int intkeyword = Integer.valueOf(keyword);
                        return invoiceRepository.getFilterWithTotalGreater(intkeyword, PageRequest.of(offset - 1, size));
                    }
                }
                else if (choice.equals("numberofitemsgreater")) {
                    if (!keyword.matches("[0-9]+"))
                        throw new WrongRequestParamException("Request param keyword should be integer only");
                    else {
                        int intkeyword = Integer.valueOf(keyword);
                        return invoiceRepository.getFilterWithCountGreater(intkeyword, PageRequest.of(offset - 1, size));
                    }
                }
            }
        }
        return invoiceRepository.getAllInvoice(userId, PageRequest.of(offset - 1, size));
    }
    public ResponseEntity<?> deleteInvoice(Long id) {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        long userId = ((UserDetailsImpl) principle).getId();

        Invoice invoice = invoiceRepository.findById(id).orElse(null);
        if (invoice == null) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: " + id + " not existed in database"));
        if (invoice.getInvoice_user().getUserId() != userId) return ResponseEntity
                .badRequest()
                .body(new MessageResponse("Error: This Invoice Id is not belong to your"));
        invoiceRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("Invoice Id : " + id + " is deleted successfully"));
    }

}
