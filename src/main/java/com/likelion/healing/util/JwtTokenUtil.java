package com.likelion.healing.util;

import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class JwtTokenUtil {

    public static String createToken(String userName, String key, long expireTimeMs) {
        Claims claims = Jwts.claims();
        claims.put("userName", userName);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTimeMs))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact()
                ;
    }

    public static Claims extractClaims(String token, String key) {
        Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        log.info("claims : {}", claims);
        return claims;
    }

    public static boolean isExpired(String token, String key) {
        try {
            Date expiredDate = extractClaims(token, key).getExpiration();
            return expiredDate.before(new Date());
        }catch (Exception e) {
            log.error("Token 유효 체크 예외 발생");
            throw new HealingSnsAppException(ErrorCode.INVALID_TOKEN, "잘못된 토큰입니다.");
        }
    }

    public static String getUserName(String token, String key) {
        return extractClaims(token, key).get("userName", String.class);
    }
}
