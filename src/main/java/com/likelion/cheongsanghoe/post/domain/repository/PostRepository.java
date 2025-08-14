package com.likelion.cheongsanghoe.post.domain.repository;

import com.likelion.cheongsanghoe.post.domain.Category;
import com.likelion.cheongsanghoe.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCategory(Category category);
    //특정 카테고리의 공 수를 카운트
    long countByCategory(Category category);
    //카테고리별 조회
    List<Post> findByCategory(Category category, Pageable pageable);
}
