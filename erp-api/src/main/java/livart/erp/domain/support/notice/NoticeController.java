package livart.erp.domain.support.notice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import livart.common.Auth.CustomUserDetails;
import livart.common.dto.response.ApiResponse;
import livart.common.mapper.SearchResult;
import livart.erp.domain.support.notice.dto.request.NoticeRegisterRequest;
import livart.erp.domain.support.notice.dto.request.NoticeSearchRequest;
import livart.erp.domain.support.notice.dto.request.NoticeUpdateRequest;
import livart.erp.domain.support.notice.dto.response.NoticeSearchResponse;
import livart.erp.domain.support.notice.dto.response.NoticeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "공지사항 관련 API", description = "✅✅ 개발 완료")
@RequestMapping("api/erp/notice")
public class NoticeController {
    private final NoticeService noticeService;

    @PostMapping("")
    @Operation(summary = "✅ 공지 사항 등록 API, 토큰 O")
    public ResponseEntity<ApiResponse<NoticeResponse>> registerNotice(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                      @RequestBody NoticeRegisterRequest request){
        NoticeResponse response = noticeService.registerNotice(customUserDetails, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping("/{noticeId}")
    @Operation(summary = "✅ 공지 사항 조회 API, 토큰 O")
    public ResponseEntity<ApiResponse<NoticeResponse>> getNotice(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                 @PathVariable Long noticeId){
        NoticeResponse response = noticeService.getNotice(customUserDetails, noticeId);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/{noticeId}")
    @Operation(summary = "✅ 공지 사항 수정 API, 토큰 O")
    public ResponseEntity<ApiResponse<NoticeResponse>> updateNotice(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                    @PathVariable Long noticeId,
                                                                    @RequestBody NoticeUpdateRequest request){
        NoticeResponse response = noticeService.updateNotice(customUserDetails, noticeId, request);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PutMapping("/status/{status}")
    @Operation(summary = "✅ 선택 공지 상태 변경 처리 API, 토큰 O")
    public ResponseEntity<ApiResponse<List<NoticeSearchResponse>>> updateStatus(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                                @PathVariable String status,
                                                                                @RequestBody List<Long> idList){
        List<NoticeSearchResponse> response = noticeService.updateStatus(customUserDetails, status, idList);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/search")
    @Operation(summary = "✅ 공지 사항 검색 API, 토큰 O")
    public ResponseEntity<ApiResponse<SearchResult<NoticeSearchResponse>>> getNoticeList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody NoticeSearchRequest request,
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            @Parameter(hidden = true) Pageable pageable){

        SearchResult<NoticeSearchResponse> response = noticeService.getNoticeList(customUserDetails, request, pageable);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @DeleteMapping("/del")
    @Operation(summary = "✅ 선택 공지사항 삭제 API, 토큰 O")
    public ResponseEntity<ApiResponse<Void>> deleteNotice(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                          @RequestBody List<Long> idList){
        noticeService.deleteNotice(customUserDetails, idList);
        return ResponseEntity.ok(ApiResponse.ok("성공적으로 삭제되었습니다."));
    }
}
