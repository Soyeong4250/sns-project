package com.likelion.healing.service;

import com.likelion.healing.domain.dto.AlarmRes;
import com.likelion.healing.domain.entity.AlarmEntity;
import com.likelion.healing.domain.entity.AlarmType;
import com.likelion.healing.domain.entity.PostEntity;
import com.likelion.healing.domain.entity.UserEntity;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.repository.AlarmRepository;
import com.likelion.healing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;

    public Page<AlarmRes> getAlarms(String userName, Pageable pageable) {

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userName)));

        return alarmRepository.findByUser(user, pageable).map(AlarmRes::of);
    }

    public void sendAlarm(UserEntity user, PostEntity post) {
        if(!user.equals(post.getUser())) {
            AlarmEntity alarm = AlarmEntity.builder()
                    .alarmType(AlarmType.NEW_LIKE_ON_POST)
                    .fromUserId(user.getId())
                    .targetId(post.getId())
                    .user(post.getUser())
                    .build();
            alarmRepository.save(alarm);
        }
    }
}
