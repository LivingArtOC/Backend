package livart.erp.domain.support.inquiry;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.support.inquiry.entity.Inquiry;
import livart.common.domain.support.inquiry.entity.QInquiry;
import livart.common.domain.support.inquiry.entity.QInquiryImage;
import livart.common.domain.support.inquiry.repository.InquiryImageRepository;
import livart.common.domain.support.inquiry.repository.InquiryRepository;
import livart.common.dto.enums.inquiry.InquiryStatus;
import livart.common.dto.enums.inquiry.InquiryType;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.erp.domain.support.inquiry.dto.request.AnswerRequest;
import livart.erp.domain.support.inquiry.dto.request.InquirySearchRequest;
import livart.erp.domain.support.inquiry.dto.response.InquiryImageResponse;
import livart.erp.domain.support.inquiry.dto.response.InquiryResponse;
import livart.erp.domain.support.inquiry.dto.response.InquirySearchResponse;
import livart.erp.domain.support.inquiry.dto.response.InquiryStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final GlobalService globalService;
    private final JPAQueryFactory jpaQueryFactory;
    private final InquiryRepository inquiryRepository;
    private final InquiryImageRepository inquiryImageRepository;

    public SearchResult<InquirySearchResponse> searchInquiry(CustomUserDetails customUserDetails, InquirySearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QInquiry inquiry = QInquiry.inquiry;
        QInquiryImage inquiryImage = QInquiryImage.inquiryImage;
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(request.getKeyword()) && request.getKey() != null){
            switch (request.getKey()) {
                case TITLE -> builder.and(inquiry.title.eq(request.getKeyword()));
                case RESPONDENT -> builder.and(inquiry.respondent.eq(request.getKeyword()));
                case QUESTIONER -> builder.and(inquiry.questioner.eq(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder keywordBuilder = new BooleanBuilder();
                    keywordBuilder.or(inquiry.questioner.eq(request.getKeyword()));
                    keywordBuilder.or(inquiry.title.eq(request.getKeyword()));
                    keywordBuilder.or(inquiry.respondent.eq(request.getKeyword()));
                    builder.and(keywordBuilder);
                }
            }
        }

        if(request.getType() != null && request.getType() != InquiryType.ALL ){
            builder.and(inquiry.type.eq(request.getType()));
        }

        if(request.getIsAnswered() != null){
            builder.and(inquiry.isAnswered.eq(request.getIsAnswered()));
        }

        if(request.getStatus() != null && request.getStatus() != InquiryStatus.ALL){
            builder.and(inquiry.status.eq(request.getStatus()));
        }
        
        if(request.getQuestionDate() != null){
            if(request.getQuestionDate().getStartDate() != null){
                builder.and(inquiry.questionAt.goe(request.getQuestionDate().getStartDate().atStartOfDay()));
            }
            if(request.getQuestionDate().getEndDate() != null){
                builder.and(inquiry.questionAt.loe(request.getQuestionDate().getEndDate().atTime(23,59,59)));
            }
        }

        if(request.getAnsweredDate() != null){
            if(request.getAnsweredDate().getStartDate() != null){
                builder.and(inquiry.answeredAt.goe(request.getAnsweredDate().getStartDate().atStartOfDay()));
            }
            if(request.getAnsweredDate().getEndDate() != null){
                builder.and(inquiry.answeredAt.loe(request.getAnsweredDate().getEndDate().atTime(23,59,59)));
            }
        }

        List<Inquiry> inquiryList = jpaQueryFactory
                .selectFrom(inquiry)
                .where(builder)
                .orderBy(inquiry.questionAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long countResult = jpaQueryFactory
                .select(inquiry.count())
                .from(inquiry)
                .where(builder)
                .fetchOne();

        long totalCount = (countResult != null) ? countResult : 0L;

        List<InquirySearchResponse> responses = inquiryList.stream()
                .map(i -> InquirySearchResponse.builder()
                        .inquiryId(i.getId())
                        .questioner(i.getQuestioner())
                        .respondent(i.getRespondent())
                        .type(i.getType())
                        .status(i.getStatus())
                        .title(i.getTitle())
                        .questionAt(i.getQuestionAt().toLocalDate())
                        .answeredAt(i.getAnsweredAt().toLocalDate())
                        .isAnswered(i.getIsAnswered())
                        .build()
                ).collect(Collectors.toList());

        return SearchResult.<InquirySearchResponse>builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }

    public InquiryResponse getInquiry(CustomUserDetails customUserDetails, Long inquiryId){
        globalService.validateAdmin(customUserDetails);

        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new CustomException(ErrorCode.INQUIRY_NOT_FOUND));
        List<InquiryImageResponse> imgList = inquiry.getImageList().stream()
                .map(img -> InquiryImageResponse.builder()
                        .imageId(img.getId())
                        .fileName(img.getFileName())
                        .imgUrl(img.getImgUrl())
                        .build()
                ).collect(Collectors.toList());

        return InquiryResponse.builder()
                .inquiryId(inquiry.getId())
                .questioner(inquiry.getQuestioner())
                .respondent(inquiry.getRespondent())
                .type(inquiry.getType())
                .status(inquiry.getStatus())
                .title(inquiry.getTitle())
                .imgList(imgList)
                .questionAt(inquiry.getQuestionAt().toLocalDate())
                .answeredAt(inquiry.getAnsweredAt().toLocalDate())
                .isAnswered(inquiry.getIsAnswered())
                .build();
    }

    @Transactional
    public InquiryResponse updateInquiry(CustomUserDetails customUserDetails, Long inquiryId, AnswerRequest request){
        globalService.validateAdmin(customUserDetails);

        Inquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new CustomException(ErrorCode.INQUIRY_NOT_FOUND));

        Boolean isAnswered = request.getAnswer() != null ? true : false;
        inquiry.updateAnswer(request.getAnswer(), customUserDetails.getUsername(), request.getStatus(), isAnswered, customUserDetails.getId(), LocalDateTime.now());

        List<InquiryImageResponse> imgList = inquiry.getImageList().stream()
                .map(img -> InquiryImageResponse.builder()
                        .imageId(img.getId())
                        .fileName(img.getFileName())
                        .imgUrl(img.getImgUrl())
                        .build()
                ).collect(Collectors.toList());

        return InquiryResponse.builder()
                .inquiryId(inquiry.getId())
                .questioner(inquiry.getQuestioner())
                .respondent(inquiry.getRespondent())
                .type(inquiry.getType())
                .status(inquiry.getStatus())
                .title(inquiry.getTitle())
                .imgList(imgList)
                .questionAt(inquiry.getQuestionAt().toLocalDate())
                .answeredAt(inquiry.getAnsweredAt().toLocalDate())
                .isAnswered(inquiry.getIsAnswered())
                .build();
    }
    
    @Transactional
    public List<InquiryStatusResponse> updateStatus(CustomUserDetails customUserDetails , List<Long> idList, String status){
        globalService.validateAdmin(customUserDetails);
        InquiryStatus updateStatus = parseStatus(status);

        return inquiryRepository.findAllById(idList).stream()
                .map(inquiry -> {
                    inquiry.updateStatus(updateStatus, customUserDetails.getId());
                    return InquiryStatusResponse.builder()
                            .inquiryId(inquiry.getId())
                            .status(inquiry.getStatus())
                            .updatedAt(inquiry.getUpdatedAt())
                            .build();
                }).collect(Collectors.toList());
    }

    public InquiryStatus parseStatus(String status){
        try{
            return InquiryStatus.valueOf(status.toUpperCase());
        }catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_INQUIRY_STATUS);
        }
    }
}
