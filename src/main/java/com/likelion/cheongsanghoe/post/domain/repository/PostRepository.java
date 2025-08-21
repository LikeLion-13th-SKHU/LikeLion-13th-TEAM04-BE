package com.likelion.cheongsanghoe.post.domain.repository;

import com.likelion.cheongsanghoe.post.domain.Category;
import com.likelion.cheongsanghoe.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    //List<Post> findByCategory(Category category);
    //특정 카테고리의 공고 수를 카운트
    long countByCategory(Category category);
    //카테고리별 조회
    Page<Post> findByCategory(Category category, Pageable pageable);

    //제목, 내용, 카테고리로 검색
    @Query("SELECT p FROM Post p WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:category IS NULL OR p.category = :category)")
    Page<Post> findKeywordAndCategory(@org.springframework.data.repository.query.Param("keyword") String keyword,
                                      @org.springframework.data.repository.query.Param("category") Category category, Pageable pageable);
}
