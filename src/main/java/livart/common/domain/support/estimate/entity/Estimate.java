package livart.common.domain.support.estimate.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.user.entity.User;
import livart.common.dto.enums.estimate.EstimateStatus;
import livart.common.dto.enums.estimate.EstimateType;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "estimate")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Estimate extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstimateStatus status;

    private String proposer; // 문의 신청자 아이디
    private Boolean isAgreed; // 약관 동의 여부
    private String companyName;
    private String managerName;
    private String phoneNum;
    private String email;
    private Boolean emailAlarm;
    private Boolean kakaoAlarm;
    private LocalDate visitDate; // 희망 방문일
    private LocalDate deliveryDate; // 희망 납품일

    private Long createdBy;
    private Long updatedBy;

    @Lob
    private String content;
    @Lob
    private String memo;

    @Builder.Default
    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstimateItem> estimateItems = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EstimateFile> estimateFiles = new ArrayList<>();

    public void update(String companyName, String managerName, String phoneNum, LocalDate visitDate, LocalDate deliveryDate, String email, String memo, EstimateStatus status, Long updatedBy){
        this.memo = memo;
        this.status = status;
        this.updatedBy = updatedBy;
        this.companyName = companyName;
        this.managerName = managerName;
        this.phoneNum = phoneNum;
        this.visitDate = visitDate;
        this.deliveryDate = deliveryDate;
        this.email = email;
    }
}
