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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmService {

    private final UserRepository userRepository;
    private final AlarmRepository alarmRepository;

    @Transactional(readOnly = true)
    public Page<AlarmRes> getAlarms(String userName, Pageable pageable) {

        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userName)));

        return alarmRepository.findByUser(user, pageable).map(AlarmRes::of);
    }

    @Transactional
    public void sendAlarm(UserEntity user, PostEntity post, AlarmType alarmType) {
        log.info("userName : {}", user.getUserName());
        log.info("postId : {}", post.getId());
        if(!user.getUserName().equals(post.getUser().getUserName())) {
            AlarmEntity alarm = AlarmEntity.builder()
                    .alarmType(alarmType)
                    .fromUserId(user.getId())
                    .targetId(post.getId())
                    .user(post.getUser())
                    .build();
            alarmRepository.save(alarm);
        }
    }
}
