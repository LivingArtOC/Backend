package livart.common.domain.social.entity;


import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.Provider;
import lombok.*;

@Table(name = "social_api")
@Getter @Entity @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SocialAPI extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private String clientId; // kakao의 경우 Rest API KEY

    @Column(nullable = false)
    private String clientSecret;

    private Long adminId;

    public void update(String clientId, String clientSecret, Long adminId) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.adminId = adminId;
    }

}
