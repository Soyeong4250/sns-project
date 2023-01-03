package com.likelion.healing.domain.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Builder
    public CommentEntity(Integer id, String comment, PostEntity post, UserEntity user) {
        this.id = id;
        this.comment = comment;
        this.post = post;
        this.user = user;
    }
}
