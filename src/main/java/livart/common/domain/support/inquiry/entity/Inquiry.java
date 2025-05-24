package livart.common.domain.support.inquiry.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.inquiry.InquiryStatus;
import livart.common.dto.enums.inquiry.InquiryType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "inquiry")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private InquiryType type;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status;

    private String questioner;
    private String respondent;
    private String title;
    private Boolean isAnswered;
    private Boolean emailAlarm;
    private Boolean kakaoAlarm;

    @Lob
    private String question;
    @Lob
    private String answer;

    private LocalDateTime questionAt;
    private LocalDateTime answeredAt;

    private Long createdBy;
    private Long updatedBy;

    @Builder.Default
    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InquiryImage> imageList = new ArrayList<>();

    public void updateAnswer(String answer,String respondent, InquiryStatus status, Boolean isAnswered, Long updatedBy, LocalDateTime answeredAt){
        this.answer = answer;
        this.respondent = respondent;
        this.status = status;
        this.isAnswered = isAnswered;
        this.updatedBy = updatedBy;
        this.answeredAt = answeredAt;
    }

    public void updateStatus(InquiryStatus status, Long updatedBy){
        this.status = status;
        this.updatedBy = updatedBy;
    }
}
