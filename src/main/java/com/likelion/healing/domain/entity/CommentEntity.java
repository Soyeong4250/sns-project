package com.likelion.healing.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name ="comment")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
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

    public void updateComment(String comment) {
        this.comment = comment;
    }
}
