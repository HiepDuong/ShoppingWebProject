package com.shoppingweb.ShoppingWeb.auditor;

import com.shoppingweb.ShoppingWeb.entity.AppUser;
import com.shoppingweb.ShoppingWeb.repository.AppUserRepository;
import com.shoppingweb.ShoppingWeb.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Component
    public class AuditorAwareImpl implements AuditorAware<AppUser> {

        @Autowired
        private AppUserRepository userRepository;

        //GetCurrentAuditor
        @Transactional(propagation = Propagation.REQUIRES_NEW) //StackOverFlow due to Recursive
        @Override
        public Optional<AppUser> getCurrentAuditor() {
            Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = ((UserDetailsImpl) principle).getUsername();
            return userRepository.findByAccount(username);
        }
    }

