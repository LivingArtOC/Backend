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
@Where(clause = "status IN ('ACTIVE', 'DORMANT')")
public class Business extends User {

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private String bizName;

    @Column(nullable = false)
    private String bizPhoneNum;

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
    private Instant bizCreatedAt;

    @LastModifiedDate
    private Instant bizUpdatedAt;
}

