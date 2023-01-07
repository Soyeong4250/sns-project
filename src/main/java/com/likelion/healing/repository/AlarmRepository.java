package com.likelion.healing.repository;

import com.likelion.healing.domain.entity.AlarmEntity;
import com.likelion.healing.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Integer> {

    Page<AlarmEntity> findByUser(UserEntity user, Pageable pageable);

}
