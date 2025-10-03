package livart.shop.domain.design.notice;

import livart.common.domain.support.notice.entity.Notice;
import livart.common.domain.support.notice.entity.NoticeImage;
import livart.common.domain.support.notice.repository.NoticeRepository;
import livart.common.dto.enums.notice.NoticeStatus;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.shop.domain.design.notice.dto.response.NoticeImageResponse;
import livart.shop.domain.design.notice.dto.response.NoticeListItemResponse;
import livart.shop.domain.design.notice.dto.response.NoticeViewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public List<NoticeListItemResponse> getAll() {
        List<Notice> all = noticeRepository.findAll(); // 공통 레포 재사용

        List<Notice> visible = all.stream()
                .filter(n -> n.getNoticeStatus() == NoticeStatus.REGISTER)
                .toList();

        if (visible.isEmpty()) {
            throw new CustomException(ErrorCode.NOTICE_NOT_FOUND);
        }

        return visible.stream()
                .sorted(Comparator
                        .comparing(Notice::getIsPinned, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(Notice::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder()))
                )
                .map(n -> NoticeListItemResponse.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .pinned(Boolean.TRUE.equals(n.getIsPinned()))
                        .registerDate(toDate(n.getCreatedAt()))
                        .build()
                )
                .toList();
    }


    public NoticeViewResponse getById(Long id) {
        Notice n = noticeRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        if (n.getNoticeStatus() != NoticeStatus.REGISTER) {
            throw new CustomException(ErrorCode.NOTICE_NOT_FOUND);
        }

        List<NoticeImageResponse> images = n.getNoticeImageList().stream()
                .map(this::toImage)
                .toList();

        return NoticeViewResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .content(n.getContent())
                .images(images)
                .registerDate(toDate(n.getCreatedAt()))
                .build();
    }

    /* ---------- 내부 유틸 ---------- */

    private NoticeImageResponse toImage(NoticeImage img) {
        return NoticeImageResponse.builder()
                .fileName(img.getFileName())
                .imageUrl(img.getImgUrl())
                .build();
    }

    private LocalDate toDate(LocalDateTime ldt) {
        return (ldt == null) ? null : ldt.toLocalDate();
    }

    /** 목록용 ETag 계산: 목록의 updatedAt(없으면 createdAt) 중 최댓값 millis */
    public long calcListEtagMillis(List<NoticeListItemResponse> list, List<Notice> raw) {
        // raw에서 max updatedAt/createdAt 구함(이미 한 번에 읽어온 데이터 재사용)
        return raw.stream()
                .map(n -> n.getUpdatedAt() != null ? n.getUpdatedAt() : n.getCreatedAt())
                .filter(t -> t != null)
                .mapToLong(TimeUtil::toEpochMilliUtc)
                .max()
                .orElse(0L);
    }

    /** 단건용 ETag 계산용도 */
    public long calcOneEtagMillis(Notice n) {
        LocalDateTime base = (n.getUpdatedAt() != null) ? n.getUpdatedAt() : n.getCreatedAt();
        return (base == null) ? 0L : TimeUtil.toEpochMilliUtc(base);
    }

    /** 컨트롤러에서 목록 ETag 계산시 raw 리스트가 필요하므로 추가함 */
    public List<Notice> getAllRaw() {
        return noticeRepository.findAll();
    }

    /* 시간 변환 유틸(중복 방지) */
    static class TimeUtil {
        static long toEpochMilliUtc(LocalDateTime ldt) {
            return ldt.atZone(java.time.ZoneOffset.UTC).toInstant().toEpochMilli();
        }
    }
}