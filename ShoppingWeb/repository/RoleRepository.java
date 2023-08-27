package com.shoppingweb.ShoppingWeb.repository;

import com.shoppingweb.ShoppingWeb.entity.AppRole;
import com.shoppingweb.ShoppingWeb.entity.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<AppRole, Long> {
    Optional<AppRole> findByName(ERole name);
}

