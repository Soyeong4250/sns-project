package com.likelion.healing.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AlgorithmServiceTest {

    private AlgorithmService algorithmService = new AlgorithmService();


    @Test
    @DisplayName("sumOfDigit 성공")
    void SumOfDigitTest() {
        assertEquals(24, algorithmService.sumOfDigit(6567));
        assertEquals(7, algorithmService.sumOfDigit(2131));
        assertEquals(12, algorithmService.sumOfDigit(138));
        assertEquals(20, algorithmService.sumOfDigit(875));
    }


}