package com.likelion.healing.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor
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
    @Schema(description = "포스트 제목")
    private String title;

    @Column(nullable = false, length = 300)
    @Schema(description = "포스트 내용")
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Schema(description = "작성자 정보")
    private UserEntity user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<CommentEntity> comments = new ArrayList<>();


    @Builder
    public PostEntity(Integer id, String title, String body, UserEntity user) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user = user;
    }

    // 정적 팩토리 메서드를 생성자로 활용
    public static PostEntity createPost(String title, String body, UserEntity user) {
        return PostEntity.builder()
                .title(title)
                .body(body)
                .user(user)
                .build();
    }

    public void updatePost(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
