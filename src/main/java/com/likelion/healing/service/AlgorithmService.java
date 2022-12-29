package com.likelion.healing.service;

import com.likelion.healing.exception.ErrorCode;
import com.likelion.healing.exception.HealingSnsAppException;
import org.springframework.stereotype.Service;

@Service
public class AlgorithmService {

    public Integer sumOfDigit(Integer num) {
        if(num <= 0) {
            throw new HealingSnsAppException(ErrorCode.BY_ZERO_ERROR, "0보다 큰 수를 입력해주세요.");
        }

        Integer sum  = 0;
        while(num > 0) {
            sum += num % 10;
            num /= 10;
        }

        return sum;
    }
}
