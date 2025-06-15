package livart.common.domain.alarm.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.user.entity.User;
import lombok.*;

@Table(name = "user_marketing_pre")
@Entity @Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMKConsent extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean smsNotice;

    @Column(nullable = false)
    private Boolean emailNotice;

    @Column(nullable = false)
    private Boolean kakaoNotice;

    @Column(nullable = false)
    private Boolean tmNotice;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
