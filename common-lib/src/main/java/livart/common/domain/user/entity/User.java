package livart.common.domain.user.entity;

import jakarta.persistence.*;
import livart.common.domain.alarm.entity.UserMKConsent;
import livart.common.domain.BaseTime;
import livart.common.domain.address.entity.UserAddress;
import livart.common.domain.term.entity.UserTerm;
import livart.common.dto.enums.user.Provider;
import livart.common.dto.enums.user.Role;
import livart.common.dto.enums.user.UserStatus;
import livart.common.dto.request.user.AdminRequest;
import livart.common.log.entity.MileageLog;
import livart.common.log.entity.UserStatusLog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Table(name = "user")
@Getter @Entity @SuperBuilder(toBuilder = true)
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;

    @Column(unique = true)
    private String loginId; // 일반 로그인 ID

    private String password; // 일반 로그인 비밀번호

    @Column(unique = true, nullable = false)
    private String phoneNum; // 전화 번호

    @Builder.Default
    private Boolean recoverable = true; // 재가입(복구 가능 여부)

    @Column(unique = true)
    private String email;

    @Builder.Default
    private Integer mileage = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Builder.Default
    @Column(nullable = false)
    private Boolean adminRegister = false;

    @Column(unique = true)
    private String socialId;

    @Lob
    private String adminMemo;

    private String refundAccNum; // 환불 계좌
    private String refundBank; // 환불 은행

    @Setter
    private LocalDateTime dormantAt;

    @Setter
    private LocalDateTime lastLoginAt;
    @Setter
    private LocalDateTime deletedAt;

    private Long updatedBy;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAddress> userAddresses = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTerm> userTerms = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserMKConsent> userMarketingNotices = new ArrayList<>();

    @Builder.Default
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserStatusLog> userStatusLogs = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MileageLog> mileageLogs = new ArrayList<>();

    public void update(String password){
        this.password = password;
    }
    public void updateStatus(UserStatus status) {this.status = status; }
    public void updatePassword(String password) {this.password = password;}

    public void updateStatusByAdmin(UserStatus status, Long updatedBy) {
        this.status = status;
        this.updatedBy = updatedBy;
    }
    public void updateMileageByAdmin(Integer mileage, Long updatedBy) {
        this.mileage = mileage;
        this.updatedBy = updatedBy;
    }

    public void updateMemo(String adminMemo, Long updatedBy){
        this.adminMemo = adminMemo;
        this.updatedBy = updatedBy;
    }

    public void updateUserFieldsFrom(AdminRequest request, String encodedPassword) {
        this.loginId = request.getLoginId();
        this.password = encodedPassword;
        this.phoneNum = request.getPhoneNum();
        this.email = request.getEmail();
        this.userName = request.getAdminName();
        this.adminMemo = request.getAdminMemo();
    }
}


