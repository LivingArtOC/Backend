package livart.common.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;


@Table(name = "consumer")
@Getter @Entity @SuperBuilder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Consumer extends User {

    private String name;

    @Column(nullable = false)
    private String phoneNum;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime consCreatedAt;

    @LastModifiedDate
    private LocalDateTime consUpdatedAt;

    public static Consumer fromUser(User user, String loginId, String phoneNum, String email) {
        return Consumer.builder()
                .id(user.getId())
                .loginId(loginId)
                .email(email)
                .role(user.getRole())
                .provider(user.getProvider())
                .status(user.getStatus())
                .adminRegister(user.getAdminRegister())
                .socialId(user.getSocialId())
                .lastLoginAt(user.getLastLoginAt())
                .deletedAt(user.getDeletedAt())
                .name(loginId)
                .phoneNum(phoneNum)
                .build();
    }

    public static Consumer ConsumerFromUser(User user, String name, String phoneNum) {
        return Consumer.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .email(user.getEmail())
                .role(user.getRole())
                .provider(user.getProvider())
                .status(user.getStatus())
                .adminRegister(user.getAdminRegister())
                .socialId(user.getSocialId())
                .lastLoginAt(user.getLastLoginAt())
                .deletedAt(user.getDeletedAt())
                .name(name)
                .phoneNum(phoneNum)
                .build();
    }
}


