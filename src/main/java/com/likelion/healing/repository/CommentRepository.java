package com.likelion.healing.repository;

import com.likelion.healing.domain.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Integer> {
    Page<CommentEntity> findByPostId(Integer postId, Pageable pageable);

    Optional<CommentEntity> findByPostIdAndId(Integer postId, Integer commentId);
}
