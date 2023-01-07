package com.likelion.healing.service;

import com.likelion.healing.domain.dto.*;
import com.likelion.healing.domain.entity.UserEntity;
import com.likelion.healing.domain.entity.UserRole;
import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import com.likelion.healing.repository.AlarmRepository;
import com.likelion.healing.repository.UserRepository;
import com.likelion.healing.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final AlarmRepository alarmRepository;

    @Value("${jwt.token.secret}")
    private String secretKey;
    private long expireTime = 1000L * 60 * 60;

    @Transactional
    public UserJoinRes join(UserJoinReq userJoinReq) {
        log.info("userName: {}", userJoinReq.getUserName());
        userRepository.findByUserName(userJoinReq.getUserName())
                .ifPresent(user -> {
                    throw new HealingSnsAppException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s은(는) 이미 있습니다.", userJoinReq.getUserName()));
                });

        UserEntity user = userRepository.save(userJoinReq.toEntity(encoder.encode(userJoinReq.getPassword())));
        return UserJoinRes.builder()
                .userId(user.getId())
                .userName(user.getUsername())
                .build();
    }

    @Transactional
    public UserLoginRes login(UserLoginReq userLoginReq) {
        log.info("userName: {}", userLoginReq.getUserName());

        UserEntity user = userRepository.findByUserName(userLoginReq.getUserName())
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userLoginReq.getUserName())));

        if(!encoder.matches(userLoginReq.getPassword(), user.getPassword())) {
            throw new HealingSnsAppException(ErrorCode.INVALID_PASSWORD, "회원 이름 또는 비밀번호를 다시 확인해주세요.");
        }

        String token = JwtTokenUtil.createToken(userLoginReq.getUserName(), secretKey, expireTime);
        return UserLoginRes.builder()
                .jwt(token)
                .build();
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", userName)));
    }

    @Transactional
    public UserRoleUpdateRes changeRole(Integer changeUserId, UserRole changeRole, String loginUserName) {

        UserEntity loginUser = userRepository.findByUserName(loginUserName)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s은(는) 없는 회원입니다.", loginUserName)));

        if (!loginUser.getRole().equals(UserRole.ADMIN)) {
            throw new HealingSnsAppException(ErrorCode.INVALID_PERMISSION, "사용자가 권한이 없습니다.");
        }

        UserEntity user = userRepository.findById(changeUserId)
                .orElseThrow(() -> new HealingSnsAppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%d번 회원은(는) 없는 회원입니다.", changeUserId)));

        user.changeRole(changeRole);
        return UserRoleUpdateRes.builder()
                                .message("권한 변경 완료")
                                .role(changeRole)
                                .build();
    }
    
}
