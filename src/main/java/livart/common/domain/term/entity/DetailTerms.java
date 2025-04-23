package livart.common.domain.term.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

import java.time.LocalDate;

@Table(name = "detail_terms")
@Entity @Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DetailTerms extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String course;
    private LocalDate startDate; //약관 적용 시작일
    private LocalDate endDate; //약관 적용 마감일
    private Boolean isExposed;
    private String officerName; //보호 책임자 이름
    private String officerPosition; //보호 책임자 직책
    private String officerPhone; //보호 책임자 전번
    private String officerEmail; //보호 책임자 이메일
    private Long updatedBy;

    @ManyToOne
    @JoinColumn(name = "terms_id", nullable = false)
    private Terms terms;

    public void updateFromUsePolicy(String course, LocalDate startDate, LocalDate endDate, Boolean isExposed,Long updatedBy){
        this.course = course;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isExposed = isExposed;
        this.updatedBy = updatedBy;
    }

    public void updateFromCourse(String officerName,
                                 String officerPosition,
                                 String officerPhone,
                                 String officerEmail,
                                 Long updatedBy){
        this.officerName = officerName;
        this.officerEmail = officerEmail;
        this.officerPhone = officerPhone;
        this.officerPosition = officerPosition;
        this.updatedBy = updatedBy;
    }

}
