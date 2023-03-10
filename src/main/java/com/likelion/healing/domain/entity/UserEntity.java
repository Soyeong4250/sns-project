package com.likelion.healing.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = "password")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE user SET deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@Schema(description = "회원")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "회원 번호")
    private Integer id;

    @Column(unique = true)
    @Schema(description = "회원 이름")
    private String userName;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Schema(description = "회원 비밀번호")
    private String password;

    @Enumerated(EnumType.STRING)
    @Schema(description = "회원 권한", defaultValue = "USER", allowableValues = {"ADMIN", "USER"})
    private UserRole role;

    @Builder
    public UserEntity(Integer id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.role = UserRole.USER;
    }

    public UserRole changeRole(UserRole role) {
        return this.role = role;
    }

}
