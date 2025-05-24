package livart.common.domain.term.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.term.TermType;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "term")
@Entity @Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Term extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Boolean isRequired;

    private String title;
    private Long updatedBy;

    @Enumerated(EnumType.STRING)
    private TermType type;

    @Lob
    private String content;

    @Builder.Default
    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserTerm> userTerms = new ArrayList<>();

    @Setter
    @OneToOne(mappedBy = "term", cascade = CascadeType.ALL, orphanRemoval = true)
    private DetailTerm detailTerm;

    public void update(String content, Long updatedBy){
        this.content = content;
        this.updatedBy = updatedBy;
    }

    public void updateOthers(String title ,String content, Long updatedBy){
        this.title = title;
        this.content = content;
        this.updatedBy = updatedBy;
    }
}
