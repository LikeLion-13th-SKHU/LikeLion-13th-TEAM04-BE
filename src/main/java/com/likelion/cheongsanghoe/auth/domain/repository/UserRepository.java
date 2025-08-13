package com.likelion.cheongsanghoe.auth.domain.repository;

import com.likelion.cheongsanghoe.auth.domain.Role;
import com.likelion.cheongsanghoe.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    @Query("SELECT u FROM User u WHERE u.name LIKE %:name%")
    List<User> findByNameContaining(@Param("name") String name);

    @Query("SELECT u FROM User u WHERE u.role = :role AND u.name LIKE %:name%")
    List<User> findByRoleAndNameContaining(@Param("role") Role role, @Param("name") String name);

    long countByRole(Role role);
}