package livart.common.domain.user.entity;

import jakarta.persistence.*;
import livart.common.domain.setting.entity.AllowedAdminIp;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Table(name = "admin")
@Getter @Entity @SuperBuilder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
    private LocalDateTime adminCreatedAt;

    @LastModifiedDate
    private LocalDateTime adminUpdatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AllowedAdminIp> allowedAdminIps = new ArrayList<>();

    public void updateLoginEnabled(boolean loginEnabled){
        this.loginEnabled = loginEnabled;
    }
}

