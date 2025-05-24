package livart.common.domain.term.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.user.entity.User;
import lombok.*;

@Table(name = "user_term")
@Entity
@Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserTerm extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean isAgreed;

    private Long updatedBy;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;
}
