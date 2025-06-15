package livart.common.domain.user.entity;

import jakarta.persistence.*;
import livart.common.domain.setting.entity.AllowedAdminIp;
import livart.erp.domain.defaultSetting.admin.dto.request.AdminRequest;
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

    private String officeNum;

    @Column(nullable = false)
    private Boolean loginEnabled;

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

    public void updateFrom(AdminRequest request, String encodedPassword) {
        super.updateUserFieldsFrom(request, encodedPassword);

        this.adminName = request.getAdminName();
        this.department = request.getDepartment();
        this.position = request.getPosition();
        this.roleTitle = request.getRoleTitle();
        this.smsNotiEnabled = request.getSmsNotiEnabled();
        this.officeNum = request.getOfficeNum();
        this.loginEnabled = request.getLoginEnabled();
    }

}

