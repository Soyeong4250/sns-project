package com.likelion.healing.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreationTimestamp
    @Column(updatable = false)
    @Schema(description = "생성시간", example = "yyyy-mm-dd hh:mm:ss")
    private Timestamp createdAt;

    @UpdateTimestamp
    @Schema(description = "마지막 수정시간", example = "yyyy-mm-dd hh:mm:ss")
    private Timestamp updatedAt;

    @Column(nullable = false)
    @Schema(description = "삭제시간", example = "yyyy-mm-dd hh:mm:ss")
    private Timestamp deleteAt;
}
