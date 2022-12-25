package com.likelion.healing.domain.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE user SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@DynamicInsert
@DynamicUpdate
@Schema(description = "회원")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "회원 번호")
    private Integer id;

    @Column(unique = true)
    @Schema(description = "회원 이름")
    private String userName;

    @Column(nullable = false)
    @Schema(description = "회원 비밀번호")
    private String password;

    @Enumerated(EnumType.STRING)
    @Schema(description = "회원 권한", defaultValue = "user", allowableValues = {"admin", "user"})
    private UserRole role;

    @Builder
    public User(String userName, String password, UserRole role) {
        this.userName = userName;
        this.password = password;
        this.role = role;
    }
}
