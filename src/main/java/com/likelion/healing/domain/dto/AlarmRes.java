package com.likelion.healing.domain.dto;

import com.likelion.healing.domain.entity.AlarmType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class AlarmRes {

    private Integer id;
    private AlarmType alarmType;
    private Integer fromUserId;
    private Integer targetId;
    private String text;
    private LocalDateTime createdAt;

    @Builder
    public AlarmRes(Integer id, AlarmType alarmType, Integer fromUserId, Integer targetId, LocalDateTime createdAt) {
        this.id = id;
        this.alarmType = alarmType;
        this.fromUserId = fromUserId;
        this.targetId = targetId;
        this.text = alarmType.label();
        this.createdAt = createdAt;
    }
}
