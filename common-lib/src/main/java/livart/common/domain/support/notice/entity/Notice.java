package livart.common.domain.support.notice.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.notice.NoticeStatus;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Table(name = "notice")
@Builder @Entity @Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notice extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private Boolean isPinned;
    private Long viewCount;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private NoticeStatus noticeStatus = NoticeStatus.REGISTER;

    @Lob
    private String content;
    private Long createdBy;
    private Long updatedBy;

    @Builder.Default
    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoticeImage> noticeImageList = new ArrayList<>();

    public void update(String title, Boolean isPinned, String content,NoticeStatus status, Long updatedBy){
        this.title = title;
        this.isPinned = isPinned;
        this.content = content;
        this.noticeStatus = status;
        this.updatedBy = updatedBy;
    }

    public void updateStatus(NoticeStatus status, Long updatedBy){
        this.noticeStatus = status;
        this.updatedBy = updatedBy;
    }
}
