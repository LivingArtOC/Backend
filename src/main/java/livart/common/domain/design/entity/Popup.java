package livart.common.domain.design.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.design.PopupType;
import livart.common.dto.enums.design.PopupStatus;
import livart.erp.domain.design.dto.request.PopupRegisterRequest;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "popup")
@Entity @Builder @Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Popup extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;

    @Enumerated(EnumType.STRING)
    private PopupStatus status;

    @Enumerated(EnumType.STRING)
    private PopupType popupType;

    private Integer topLocationPixel;
    private Integer leftLocationPixel;
    private Boolean isHiddenToday;
    private Integer widthPixel;
    private Integer heightPixel;
    private String pageUrl;
    private String parameter;
    private Long createdUserId; // 생성한 사람
    private Long updatedUserId; // 수정한 사람
    private LocalDateTime exposedStartDate; // 노출 시작 시간
    private LocalDateTime exposedEndDate; // 노출 마감 시간

    public void update(PopupRegisterRequest request, Long updatedUserId) {
        this.title = request.getTitle();
        this.status = request.getStatus();
        this.popupType = request.getPopupType();
        this.topLocationPixel = request.getTopLocationPixel();
        this.leftLocationPixel = request.getLeftLocationPixel();
        this.isHiddenToday = request.getIsHiddenToday();
        this.widthPixel = request.getWidthPixel();
        this.heightPixel = request.getHeightPixel();
        this.pageUrl = request.getPageUrl();
        this.parameter = request.getParameter();
        this.exposedStartDate = request.getStart();
        this.exposedEndDate = request.getEnd();
        this.updatedUserId = updatedUserId;
    }

    public void updateStatus(PopupStatus status){
        this.status = status;
    }
}
