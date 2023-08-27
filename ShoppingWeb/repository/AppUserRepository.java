package com.shoppingweb.ShoppingWeb.repository;

import com.shoppingweb.ShoppingWeb.entity.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByAccount(String account);
    Boolean existsByAccount(String account);
    Boolean existsByEmail(String email);

    Boolean existsByPhone(String phone);
//FOR ADMIN
    @Query(value = "SELECT e FROM AppUser e JOIN e.roles r WHERE e.fullname LIKE %?1% AND r.id = 2")
    public Page<AppUser> filterByAdminName(String keyword, Pageable pageable);
    @Query(value = "SELECT e FROM AppUser e JOIN e.roles r WHERE e.userId LIKE %?1% AND r.id = '2'")
    public Page<AppUser> filterByAdminId(String keyword, Pageable pageable);

    @Query(value = "SELECT e FROM AppUser e JOIN e.roles r WHERE e.phone LIKE %?1% AND r.id = '2'")
    public Page<AppUser> filterByAdminPhone(String keyword, Pageable pageable);

    @Query(value = "SELECT e FROM AppUser e JOIN e.roles r WHERE e.email LIKE %?1% AND r.id = '2'")
    public Page<AppUser> filterByAdminEmail(String keyword, Pageable pageable);

    @Query(value = "SELECT e FROM AppUser e JOIN e.roles r WHERE e.sex LIKE %?1% AND r.id = '2'")
    public Page<AppUser> filterByAdminSex(String keyword, Pageable pageable);

    @Query(value = "SELECT e FROM AppUser e JOIN e.roles r WHERE r.id = '2'")
    public Page<AppUser> filterByAdminOnly(Pageable pageable);

    //FOR User Filteration
    @Query(value = "SELECT e FROM AppUser e JOIN e.roles r WHERE e.fullname LIKE %?1% AND r.id = 1")
    public Page<AppUser> filterByUserName(String keyword, Pageable pageable);
    @Query(value = "SELECT e FROM AppUser e JOIN e.roles r WHERE e.userId LIKE %?1% AND r.id = 1")
    public Page<AppUser> filterByUserId(String keyword, Pageable pageable);

    @Query(value = "SELECT e FROM AppUser e JOIN e.roles r WHERE e.phone LIKE %?1% AND r.id = 1")
    public Page<AppUser> filterByUserPhone(String keyword, Pageable pageable);

    @Query(value = "SELECT e FROM AppUser e JOIN e.roles r WHERE e.email LIKE %?1% AND r.id = 1")
    public Page<AppUser> filterByUserEmail(String keyword, Pageable pageable);

    @Query(value = "SELECT e FROM AppUser e JOIN e.roles r WHERE e.sex LIKE %?1% AND r.id = 1")
    public Page<AppUser> filterByUserSex(String keyword, Pageable pageable);

    @Query(value = "SELECT e FROM AppUser e JOIN e.roles r WHERE r.id = '2'")
    public Page<AppUser> filterByUserOnly(Pageable pageable);
}
