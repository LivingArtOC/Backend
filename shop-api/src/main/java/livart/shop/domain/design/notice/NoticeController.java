package livart.shop.domain.design.notice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.dto.response.ApiResponse;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.shop.domain.design.notice.dto.response.NoticeListItemResponse;
import livart.shop.domain.design.notice.dto.response.NoticeViewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 쇼핑몰 공지사항 조회 전용 컨트롤러
 * - 목록: pinned 우선 + createdAt desc
 * - 상세: ERP 저장 content/이미지 그대로 반환
 * - ETag: updatedAt 기반 epoch milli 사용
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shop/design/notice")
@CrossOrigin(origins = "*")
@Tag(name = "공지사항 API")
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping()
    @Operation(summary = "공지사항 전체 목록(<=100개 가정, 공개)")
    public ResponseEntity<ApiResponse<List<NoticeListItemResponse>>> getAll(
            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch) {

        // 한 번에 전부 로딩
        var raw = noticeService.getAllRaw().stream().toList();
        var list = noticeService.getAll();

        if (list.isEmpty()) {
            throw new CustomException(ErrorCode.NOTICE_NOT_FOUND);
        }

        long etagMillis = noticeService.calcListEtagMillis(list, raw);
        String etag = "\"" + etagMillis + "\"";

        if (etag.equals(ifNoneMatch)) {
            // 변경 없음
            return ResponseEntity.status(304).eTag(etag).build();
        }
        return ResponseEntity.ok().eTag(etag).body(ApiResponse.ok(list));
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "공지사항 상세(공개)")
    public ResponseEntity<ApiResponse<NoticeViewResponse>> getOne(
            @PathVariable Long noticeId,
            @RequestHeader(value = HttpHeaders.IF_NONE_MATCH, required = false) String ifNoneMatch) {

        var raw = noticeService.getAllRaw().stream()
                .filter(n -> n.getId().equals(noticeId))
                .findFirst()
                .orElse(null);

        if (raw == null) throw new CustomException(ErrorCode.NOTICE_NOT_FOUND);

        long etagMillis = noticeService.calcOneEtagMillis(raw);
        String etag = "\"" + etagMillis + "\"";

        if (etag.equals(ifNoneMatch)) {
            return ResponseEntity.status(304).eTag(etag).build();
        }

        NoticeViewResponse body = noticeService.getById(noticeId);
        return ResponseEntity.ok().eTag(etag).body(ApiResponse.ok(body));
    }
}