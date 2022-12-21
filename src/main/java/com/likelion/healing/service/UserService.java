package com.likelion.healing.service;

import com.likelion.healing.domain.dto.UserJoinReq;
import com.likelion.healing.domain.dto.UserJoinRes;
import com.likelion.healing.domain.dto.UserLoginReq;
import com.likelion.healing.domain.dto.UserLoginRes;
import com.likelion.healing.domain.entity.User;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.repository.UserRepository;
import com.likelion.healing.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("secretKey")
    private String secretKey;
    private long expireTime = 1000L * 60 * 60;

    public UserJoinRes join(UserJoinReq userJoinReq) {
        log.info("userName: {}", userJoinReq.getUserName());
        userRepository.findByUserName(userJoinReq.getUserName())
                .ifPresent(user -> {
                    throw new HealingSnsAppException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s은(는) 이미 있습니다.", userJoinReq.getUserName()));
                });

        User user = userRepository.save(userJoinReq.toEntity(encoder.encode(userJoinReq.getPassword())));
        return UserJoinRes.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .build();
    }

    public UserLoginRes login(UserLoginReq userLoginReq) {
        log.info("userName: {}", userLoginReq.getUserName());

        User user = userRepository.findByUserName(userLoginReq.getUserName())
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userLoginReq.getUserName())));

        if(!encoder.matches(userLoginReq.getPassword(), user.getPassword())) {
            throw new HealingSnsAppException(ErrorCode.INVALID_PASSWORD, "회원 이름 또는 비밀번호를 다시 확인해주세요.");
        }

        String token = JwtTokenUtil.createToken(userLoginReq.getUserName(), secretKey, expireTime);
        return UserLoginRes.builder()
                .jwt(token)
                .build();
    }
}
