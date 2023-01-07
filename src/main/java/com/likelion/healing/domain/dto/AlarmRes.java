package com.likelion.healing.domain.dto;

import com.likelion.healing.domain.entity.AlarmEntity;
import com.likelion.healing.domain.entity.AlarmType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class AlarmRes {

    @Schema(description = "알람 번호")
    private Integer id;
    @Schema(description = "알람 타입", allowableValues = {"AlarmType.NEW_COMMENT_ON_POST", "AlarmType.NEW_LIKE_ON_POST"})
    private AlarmType alarmType;
    @Schema(description = "알람을 발생시킨 회원 번호")
    private Integer fromUserId;
    @Schema(description = "알람이 발생한 포스트 번호")
    private Integer targetId;
    @Schema(description = "알람 메세지")
    private String text;
    @Schema(description = "알람 생성시간")
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

    public static AlarmRes of(AlarmEntity alarm) {
        return AlarmRes.builder()
                .id(alarm.getId())
                .alarmType(alarm.getAlarmType())
                .fromUserId(alarm.getFromUserId())
                .targetId(alarm.getTargetId())
                .createdAt(alarm.getCreatedAt())
                .build();
    }
}
