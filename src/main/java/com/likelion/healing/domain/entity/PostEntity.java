package com.likelion.healing.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


@Entity
@Table(name = "post")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE post SET deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@Schema(description = "포스트")
public class PostEntity extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "포스트 번호")
    private Integer id;

    @Column(nullable = false)
    @NotBlank
    @Schema(description = "포스트 제목")
    private String title;

    @Column(nullable = false, length = 300)
    @NotBlank
    @Schema(description = "포스트 내용")
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotBlank
    @Schema(description = "작성자 정보")
    private User user;

    public void updatePost(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public void deletePost() {
        setDeletedAt(LocalDateTime.now());
    }
}
