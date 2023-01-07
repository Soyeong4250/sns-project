package com.likelion.healing.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "alarm")
@NoArgsConstructor
@Getter
public class AlarmEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @Column(nullable = false)
    private Integer fromUserId;

    @Column(nullable = false)
    private Integer targetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Builder
    public AlarmEntity(Integer id, AlarmType alarmType, Integer fromUserId, Integer targetId, UserEntity user) {
        this.id = id;
        this.alarmType = alarmType;
        this.fromUserId = fromUserId;
        this.targetId = targetId;
        this.user = user;
    }
}
