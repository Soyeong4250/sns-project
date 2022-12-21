package com.likelion.healing.service;

import com.likelion.healing.domain.dto.UserJoinReq;
import com.likelion.healing.domain.dto.UserJoinRes;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserJoinRes join(UserJoinReq userJoinReq) {
        log.info("userName: {}", userJoinReq.getUserName());
        userRepository.findByUserName(userJoinReq.getUserName())
                .ifPresent(user -> {
                    throw new HealingSnsAppException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s는 이미 있습니다.", userJoinReq.getUserName()));
                });
        // User user = userRepository.save(userJoinReq.toEntity());
        return null;
    }
}
