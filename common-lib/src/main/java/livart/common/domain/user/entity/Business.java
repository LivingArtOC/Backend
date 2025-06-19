package livart.common.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;


@Table(name = "business")
@Getter @Entity @SuperBuilder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Business extends User {

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private String bizName;

    @Column(nullable = false, unique = true)
    private String bizRegistrationNum;

    @Column(nullable = false)
    private String bizStatus;

    @Column(nullable = false)
    private String bizType;

    private String faxNum;

    @Column(nullable = false)
    private String managerName;

    @Column(nullable = false)
    private String managerPhoneNum;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime bizCreatedAt;

    @LastModifiedDate
    private LocalDateTime bizUpdatedAt;

    public static Business businessFromUser(User user, String ownerName, String bizName, String bizRegistrationNum,
                                            String bizStatus, String bizType, String faxNum, String managerName, String managerPhoneNum) {
        return Business.builder()
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
                .ownerName(ownerName)
                .bizName(bizName)
                .bizRegistrationNum(bizRegistrationNum)
                .bizStatus(bizStatus)
                .bizType(bizType)
                .faxNum(faxNum)
                .managerName(managerName)
                .managerPhoneNum(managerPhoneNum)
                .build();
    }
}

