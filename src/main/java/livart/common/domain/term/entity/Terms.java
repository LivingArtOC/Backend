package livart.common.domain.term.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.notice.entity.UserMKConsent;
import livart.common.dto.enums.Required;
import livart.common.dto.enums.TermType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "terms")
@Entity @Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Terms extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Required isRequired;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private TermType type;

    @Lob
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @OneToMany(mappedBy = "terms", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTerms> userTerms;

    @OneToMany(mappedBy = "terms", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetailTerms> detailTerms;

    public void update(String content){
        this.content = content;
    }
}
