package livart.erp.domain.support.notice;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.support.notice.entity.Notice;
import livart.common.domain.support.notice.entity.NoticeImage;
import livart.common.domain.support.notice.entity.QNotice;
import livart.common.domain.support.notice.repository.NoticeImageRepository;
import livart.common.domain.support.notice.repository.NoticeRepository;
import livart.common.dto.enums.notice.NoticeStatus;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.erp.domain.support.notice.dto.request.NoticeRegisterRequest;
import livart.erp.domain.support.notice.dto.request.NoticeSearchRequest;
import livart.erp.domain.support.notice.dto.request.NoticeUpdateRequest;
import livart.erp.domain.support.notice.dto.response.ImageResponse;
import livart.erp.domain.support.notice.dto.response.NoticeResponse;
import livart.erp.domain.support.notice.dto.response.NoticeSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final GlobalService globalService;
    private final NoticeRepository noticeRepository;
    private final NoticeImageRepository noticeImageRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public NoticeResponse registerNotice(CustomUserDetails customUserDetails, NoticeRegisterRequest request){
        globalService.validateAdmin(customUserDetails);

        Notice notice = Notice.builder()
                .title(request.getTitle())
                .isPinned(request.getIsPinned())
                .content(request.getContent())
                .author(customUserDetails.getUsername())
                .noticeStatus(NoticeStatus.REGISTER)
                .viewCount(0L)
                .createdBy(customUserDetails.getId())
                .build();

        List<NoticeImage> imageList = request.getAttachment().stream()
                .map(img -> NoticeImage.builder()
                        .imgUrl(img.getImgUrl())
                        .fileName(img.getFileName())
                        .createdBy(customUserDetails.getId())
                        .notice(notice)
                        .build()).collect(Collectors.toList());

        notice.getNoticeImageList().addAll(imageList);

        Notice saved = noticeRepository.save(notice);

        List<ImageResponse> imgResponse = imageList.stream()
                    .map(img -> ImageResponse.builder()
                            .imageId(img.getId())
                            .fileName(img.getFileName())
                            .imgUrl(img.getImgUrl())
                            .build()).collect(Collectors.toList());

        return NoticeResponse.builder()
                .noticeId(saved.getId())
                .title(saved.getTitle())
                .isPinned(saved.getIsPinned())
                .author(saved.getAuthor())
                .content(saved.getContent())
                .status(saved.getNoticeStatus())
                .viewCount(saved.getViewCount())
                .imgList(imgResponse)
                .registerDate(saved.getCreatedAt().toLocalDate())
                .build();
    }

    public NoticeResponse getNotice(CustomUserDetails customUserDetails, Long noticeId){
        globalService.validateAdmin(customUserDetails);

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        List<ImageResponse> imgResponse = notice.getNoticeImageList().stream()
                .map(img -> ImageResponse.builder()
                        .imageId(img.getId())
                        .fileName(img.getFileName())
                        .imgUrl(img.getImgUrl())
                        .build()
                ).collect(Collectors.toList());

        return NoticeResponse.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .author(notice.getAuthor())
                .isPinned(notice.getIsPinned())
                .content(notice.getContent())
                .status(notice.getNoticeStatus())
                .viewCount(notice.getViewCount())
                .imgList(imgResponse)
                .registerDate(notice.getCreatedAt().toLocalDate())
                .build();
    }

    @Transactional
    public NoticeResponse updateNotice(CustomUserDetails customUserDetails, Long noticeId , NoticeUpdateRequest request){
        globalService.validateAdmin(customUserDetails);

        Notice notice = noticeRepository.findById(noticeId).orElseThrow(() -> new CustomException(ErrorCode.NOTICE_NOT_FOUND));

        notice.update(request.getTitle(), request.getIsPinned(), request.getContent(), request.getStatus(), customUserDetails.getId());

        noticeImageRepository.deleteAllByNotice(notice);

        List<NoticeImage> newImage = request.getAttachment().stream()
                .map(img -> NoticeImage.builder()
                        .imgUrl(img.getImgUrl())
                        .fileName(img.getFileName())
                        .createdBy(customUserDetails.getId())
                        .notice(notice)
                        .build()).collect(Collectors.toList());

        noticeImageRepository.saveAll(newImage);

        notice.getNoticeImageList().addAll(newImage);

        List<ImageResponse> imgResponse = newImage.stream()
                .map(img -> ImageResponse.builder()
                        .imageId(img.getId())
                        .fileName(img.getFileName())
                        .imgUrl(img.getImgUrl())
                        .build()
                ).collect(Collectors.toList());

        return NoticeResponse.builder()
                .noticeId(notice.getId())
                .title(notice.getTitle())
                .author(notice.getAuthor())
                .isPinned(notice.getIsPinned())
                .content(notice.getContent())
                .status(notice.getNoticeStatus())
                .viewCount(notice.getViewCount())
                .imgList(imgResponse)
                .registerDate(notice.getCreatedAt().toLocalDate())
                .build();
    }

    public List<NoticeSearchResponse> updateStatus(CustomUserDetails customUserDetails, String status, List<Long> idList){
        globalService.validateAdmin(customUserDetails);

        NoticeStatus noticeStatus = parseNoticeStatus(status);

        List<Notice> notices = noticeRepository.findAllById(idList).stream()
                .map(notice -> {
                    notice.updateStatus(noticeStatus,customUserDetails.getId());
                    return notice;
                }).collect(Collectors.toList());

        return notices.stream()
                .map(notice -> NoticeSearchResponse.builder()
                        .noticeId(notice.getId())
                        .title(notice.getTitle())
                        .author(notice.getAuthor())
                        .isPinned(notice.getIsPinned())
                        .status(notice.getNoticeStatus())
                        .registerDate(notice.getCreatedAt().toLocalDate())
                        .build()
                ).collect(Collectors.toList());
    }

    private NoticeStatus parseNoticeStatus(String status) {
        try {
            return NoticeStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_NOTICE_STATUS);
        }
    }

    public SearchResult<NoticeSearchResponse> getNoticeList(CustomUserDetails customUserDetails, NoticeSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QNotice notice = QNotice.notice;
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(request.getKeyword()) && request.getKey() != null){
            switch (request.getKey()) {
                case AUTHOR -> builder.and(notice.author.containsIgnoreCase(request.getKeyword()));
                case TITLE -> builder.and(notice.title.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(notice.author.containsIgnoreCase(request.getKeyword()));
                    keywordBuilder.or(notice.title.containsIgnoreCase(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if (request.getIsPinned() != null){
            builder.and(notice.isPinned.eq(request.getIsPinned()));
        }

        if (request.getStatus() != null && request.getStatus() != NoticeStatus.ALL){
            builder.and(notice.noticeStatus.eq(request.getStatus()));
        }

        if (request.getRegisterDate() != null) {
            if (request.getRegisterDate().getStartDate() != null) {
                builder.and(notice.createdAt.goe(request.getRegisterDate().getStartDate().atStartOfDay()));
            }
            if (request.getRegisterDate().getEndDate() != null) {
                builder.and(notice.createdAt.loe(request.getRegisterDate().getEndDate().atTime(23, 59, 59)));
            }
        }

        List<Notice> notices = jpaQueryFactory
                .selectFrom(notice)
                .where(builder)
                .orderBy(notice.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long countResult = jpaQueryFactory
                .select(notice.count())
                .from(notice)
                .where(builder)
                .fetchOne();

        long totalCount = (countResult != null) ? countResult : 0L;

        List<NoticeSearchResponse> responses = notices.stream()
                .map(n -> NoticeSearchResponse.builder()
                        .noticeId(n.getId())
                        .title(n.getTitle())
                        .author(n.getAuthor())
                        .isPinned(n.getIsPinned())
                        .status(n.getNoticeStatus())
                        .registerDate(n.getCreatedAt().toLocalDate())
                        .build()
                ).collect(Collectors.toList());

        return SearchResult.<NoticeSearchResponse>builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }
}
