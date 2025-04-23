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


@Table(name = "admin")
@Getter @Entity @SuperBuilder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Where(clause = "status IN ('ACTIVE', 'DORMANT')")
public class Admin extends User {

    @Column(nullable = false)
    private String adminName;

    private String department;

    private String position;

    private String roleTitle;

    @Column(nullable = false)
    private Boolean smsNotiEnabled;

    @Column(nullable = false)
    private String phoneNum;

    private String officeNum;

    @Column(nullable = false)
    private Boolean loginEnabled;

    private String adminMemo;

    @CreatedDate
    @Column(updatable = false)
    private Instant adminCreatedAt;

    @LastModifiedDate
    private Instant adminUpdatedAt;

    public void updateLoginEnabled(boolean loginEnabled){
        this.loginEnabled = loginEnabled;
    }
}

