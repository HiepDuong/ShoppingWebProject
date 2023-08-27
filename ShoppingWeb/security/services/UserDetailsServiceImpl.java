package com.shoppingweb.ShoppingWeb.security.services;

import com.shoppingweb.ShoppingWeb.entity.AppUser;
import com.shoppingweb.ShoppingWeb.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    AppUserRepository appUserRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByAccount(account)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with account: " + account));

        return UserDetailsImpl.build(user);
    }
}
