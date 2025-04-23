package livart.common.log.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "admin_action_logs")
@Entity @Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminActionLog extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long adminId;         // 운영자 ID
    private String adminLoginId;  // 운영자 아이디
    private ActionType actionType;    // GET , UPDATE, DELETE
    private String page;          // 요청 URI
    private String targetTable;   // 예: "user", "admin"
    private Long targetId;        // 조작 대상 ID
    private String ipAddress;     // 운영자 IP
}
