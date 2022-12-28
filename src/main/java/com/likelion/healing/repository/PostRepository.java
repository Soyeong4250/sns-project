package com.likelion.healing.repository;

import com.likelion.healing.domain.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Integer> {
    Page<PostEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
