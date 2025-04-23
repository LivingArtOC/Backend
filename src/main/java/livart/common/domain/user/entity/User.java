package livart.common.domain.user.entity;

import jakarta.persistence.*;
import livart.common.domain.notice.entity.UserMKConsent;
import livart.common.domain.BaseTime;
import livart.common.domain.address.entity.UserAddress;
import livart.common.domain.term.entity.UserTerms;
import livart.common.dto.enums.Provider;
import livart.common.dto.enums.Role;
import livart.common.dto.enums.UserStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Table(name = "user")
@Getter @Entity @SuperBuilder(toBuilder = true)
@Where(clause = "status IN ('ACTIVE', 'DORMANT')")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String loginId; // 일반 로그인 ID

    private String password; // 일반 로그인 비밀번호

    private String email;

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

    @Setter
    private LocalDateTime lastLoginAt;
    @Setter
    private LocalDateTime deletedAt;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAddress> userAddresses = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTerms> userTerms = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserMKConsent> userMarketingNotices = new ArrayList<>();

    public void update(String password){
        this.password = password;
    }
    public void updateStatus(UserStatus status) {this.status = status; }
}


