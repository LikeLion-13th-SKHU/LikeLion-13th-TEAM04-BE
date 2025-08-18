package com.likelion.cheongsanghoe.member.domain.repository;

import com.likelion.cheongsanghoe.auth.domain.User;
import com.likelion.cheongsanghoe.member.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUser(User user);

    @Query("SELECT m FROM Member m WHERE m.user.id = :userId")
    Optional<Member> findByUserId(@Param("userId") Long userId);

    @Query("SELECT m FROM Member m WHERE m.user.email = :email")
    Optional<Member> findByUserEmail(@Param("email") String email);

    Optional<Member> findByNickname(String nickname);

    boolean existsByNickname(String nickname);

    Optional<Member> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT m FROM Member m WHERE m.user.role = :role")
    List<Member> findByUserRole(@Param("role") com.likelion.cheongsanghoe.auth.domain.Role role);

    @Query("SELECT m FROM Member m WHERE m.user.role = :role")
    Page<Member> findByUserRole(@Param("role") com.likelion.cheongsanghoe.auth.domain.Role role, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.status = 'ACTIVE' AND m.nickname LIKE %:keyword%")
    Page<Member> searchByNickname(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.status = 'ACTIVE' AND m.address LIKE %:address%")
    Page<Member> findByAddressContaining(@Param("address") String address, Pageable pageable);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.user.role = :role AND m.status = 'ACTIVE'")
    Long countActiveByRole(@Param("role") com.likelion.cheongsanghoe.auth.domain.Role role);

    // 전체 활성 회원 수(추가함)
    @Query("SELECT COUNT(m) FROM Member m WHERE m.user.role IS NOT NULL AND m.status = 'ACTIVE'")
    Long countActiveMembersWithRole();

    @Query("SELECT m FROM Member m WHERE m.status = 'ACTIVE' AND " +
            "(m.nickname LIKE %:keyword% OR m.address LIKE %:keyword% OR " +
            "m.jobCategory LIKE %:keyword% OR m.skills LIKE %:keyword% OR m.bio LIKE %:keyword%)")
    Page<Member> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
