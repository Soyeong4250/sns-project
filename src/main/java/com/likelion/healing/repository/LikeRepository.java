package com.likelion.healing.repository;

import com.likelion.healing.domain.entity.LikeEntity;
import com.likelion.healing.domain.entity.PostEntity;
import com.likelion.healing.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Integer> {

    Optional<LikeEntity> findByPostAndUser(PostEntity post, UserEntity user);

    @Query("select count(l) from LikeEntity l where l.post = :post")
    Integer findByPost(@Param("post") PostEntity post);
}
