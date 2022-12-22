package com.likelion.healing.repository;

import com.likelion.healing.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
