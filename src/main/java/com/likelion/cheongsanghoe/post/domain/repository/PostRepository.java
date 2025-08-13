package com.likelion.cheongsanghoe.post.domain.repository;

import com.likelion.cheongsanghoe.post.domain.Category;
import com.likelion.cheongsanghoe.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCategory(Category category);
}
