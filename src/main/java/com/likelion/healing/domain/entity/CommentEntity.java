package com.likelion.healing.domain.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name ="comment")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE comment SET deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
public class CommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @Setter
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Setter
    private UserEntity user;

    @Builder
    public CommentEntity(Integer id, String comment, PostEntity post, UserEntity user) {
        this.id = id;
        this.comment = comment;
        this.post = post;
        this.user = user;
    }

    // 정적 팩토리 메서드를 생성자로 활용
    public static CommentEntity createComment(String comment, PostEntity post, UserEntity user) {
        return CommentEntity.builder()
                .comment(comment)
                .post(post)
                .user(user)
                .build();
    }

    public void updateComment(String comment, UserEntity user) {
        this.comment = comment;
        this.user = user;
    }

}
