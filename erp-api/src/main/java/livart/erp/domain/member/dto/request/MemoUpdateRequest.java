package livart.erp.domain.member.dto.request;

import lombok.Getter;

@Getter
public class MemoUpdateRequest {
    private Long userId;
    private String adminMemo;
}
