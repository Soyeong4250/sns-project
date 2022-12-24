package com.likelion.healing.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    @Schema(description = "생성시간", example = "yyyy-mm-dd hh:mm:ss")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Schema(description = "마지막 수정시간", example = "yyyy-mm-dd hh:mm:ss")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제시간", example = "yyyy-mm-dd hh:mm:ss")
    private LocalDateTime deletedAt;
}
