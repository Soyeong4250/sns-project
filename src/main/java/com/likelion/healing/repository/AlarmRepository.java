package com.likelion.healing.repository;

import com.likelion.healing.domain.dto.AlarmRes;
import com.likelion.healing.domain.entity.AlarmEntity;
import com.likelion.healing.domain.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<AlarmEntity, Integer> {

    Page<AlarmRes> findByUser(UserEntity user, Pageable pageable);

}
