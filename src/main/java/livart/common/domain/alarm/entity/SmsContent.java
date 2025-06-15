package livart.common.domain.alarm.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import lombok.*;

@Table(name = "sms_content")
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SmsContent extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;
    private Long updatedBy;

    public void update(String title, String content, Long updatedBy){
        this.title = title;
        this.content = content;
        this.updatedBy = updatedBy;
    }
}
