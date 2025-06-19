package livart.common.domain.term.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

import java.time.LocalDate;

@Table(name = "detail_term")
@Entity @Getter @Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DetailTerm extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String course; // 개인정보 처리방침
    private LocalDate startDate; //약관 적용 시작일
    private LocalDate endDate; //약관 적용 마감일
    private Boolean isExposed; // 공정 거래 위원회 로고 하단 푸터 노출 여부
    private String officerName; //보호 책임자 이름
    private String officerPosition; //보호 책임자 직책
    private String officerPhone; //보호 책임자 전번
    private String officerEmail; //보호 책임자 이메일
    private Long updatedBy;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    public void updateFromUsePolicy(String course, LocalDate startDate, LocalDate endDate, Boolean isExposed,
                                    String officerName,
                                    String officerPosition,
                                    String officerPhone,
                                    String officerEmail,
                                    Long updatedBy){
        this.course = course;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isExposed = isExposed;
        this.officerName = officerName;
        this.officerEmail = officerEmail;
        this.officerPhone = officerPhone;
        this.officerPosition = officerPosition;
        this.updatedBy = updatedBy;
    }

}
