package livart.erp.domain.support.faq;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.support.faq.entity.FAQ;
import livart.common.domain.support.faq.entity.QFAQ;
import livart.common.domain.support.faq.repository.FAQRepository;
import livart.common.dto.enums.as.FAQStatus;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.common.util.QuerydslSortUtil;
import livart.erp.domain.support.faq.dto.request.FAQRegisterRequest;
import livart.erp.domain.support.faq.dto.request.FAQSearchRequest;
import livart.erp.domain.support.faq.dto.request.FAQUpdateRequest;
import livart.erp.domain.support.faq.dto.response.FAQResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FAQService {
    private final GlobalService globalService;
    private final FAQRepository faqRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public FAQResponse registerFAQ(CustomUserDetails customUserDetails, FAQRegisterRequest request){
        globalService.validateAdmin(customUserDetails);

        Boolean isAnswered = request.getAnswer() != null ? true : false;
        LocalDateTime answeredAt = request.getAnswer() != null ? LocalDateTime.now() : null;
        String respondent = request.getAnswer() != null ? customUserDetails.getUsername() : null;

        FAQ faq = FAQ.builder()
                .questioner(customUserDetails.getUsername())
                .respondent(respondent)
                .type(request.getType())
                .isAnswered(isAnswered)
                .status(FAQStatus.REGISTER)
                .question(request.getQuestion())
                .answer(request.getAnswer())
                .createdBy(customUserDetails.getId())
                .questionAt(LocalDateTime.now())
                .answeredAt(answeredAt)
                .build();

        FAQ saved = faqRepository.save(faq);

        return FAQResponse.builder()
                .faqId(saved.getId())
                .questioner(saved.getQuestioner())
                .respondent(saved.getRespondent())
                .isAnswered(isAnswered)
                .type(saved.getType())
                .status(saved.getStatus())
                .question(saved.getQuestion())
                .answer(saved.getAnswer())
                .questionAt(LocalDate.now())
                .answeredAt(answeredAt.toLocalDate())
                .build();

    }

    public FAQResponse getFAQ(CustomUserDetails customUserDetails, Long faqId){
        globalService.validateAdmin(customUserDetails);

        FAQ faq = faqRepository.findById(faqId).orElseThrow(() -> new CustomException(ErrorCode.FAQ_NOT_FOUND));

        return FAQResponse.builder()
                .faqId(faq.getId())
                .questioner(faq.getQuestioner())
                .respondent(faq.getRespondent())
                .isAnswered(faq.getIsAnswered())
                .type(faq.getType())
                .status(faq.getStatus())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .questionAt(faq.getQuestionAt().toLocalDate())
                .answeredAt(faq.getAnsweredAt().toLocalDate())
                .build();
    }

    public FAQResponse updateFAQ(CustomUserDetails customUserDetails, Long faqId, FAQUpdateRequest request){
        globalService.validateAdmin(customUserDetails);

        FAQ faq = faqRepository.findById(faqId).orElseThrow(() -> new CustomException(ErrorCode.FAQ_NOT_FOUND));

        Boolean isAnswered = request.getAnswer() != null ? true : false;
        LocalDateTime answeredAt = request.getAnswer() != null ? LocalDateTime.now() : null;
        String respondent = request.getAnswer() != null ? customUserDetails.getUsername() : null;

        faq.update(respondent, request.getType(), request.getStatus(), request.getQuestion(), request.getAnswer(), customUserDetails.getId(), isAnswered, answeredAt);
        faqRepository.save(faq);

        return FAQResponse.builder()
                .faqId(faq.getId())
                .questioner(faq.getQuestioner())
                .respondent(faq.getRespondent())
                .isAnswered(faq.getIsAnswered())
                .type(faq.getType())
                .status(faq.getStatus())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .questionAt(faq.getQuestionAt().toLocalDate())
                .answeredAt(faq.getAnsweredAt().toLocalDate())
                .build();
    }

    public SearchResult<FAQResponse> searchFAQ(CustomUserDetails customUserDetails, FAQSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QFAQ faq = QFAQ.fAQ;
        BooleanBuilder builder = new BooleanBuilder();

        if(request.getType() != null){
            builder.and(faq.type.eq(request.getType()));
        }

        if(request.getStatus() != null && request.getStatus() != FAQStatus.ALL){
            builder.and(faq.status.eq(request.getStatus()));
        }

        if(request.getQuestionDate() != null){
            if(request.getQuestionDate().getStartDate() != null){
                builder.and(faq.questionAt.goe(request.getQuestionDate().getStartDate().atStartOfDay()));
            }
            if(request.getQuestionDate().getEndDate() != null){
                builder.and(faq.questionAt.loe(request.getQuestionDate().getEndDate().atTime(23,59,59)));
            }
        }
        OrderSpecifier<?>[] orderSpecifiers = QuerydslSortUtil.getOrderSpecifiers(pageable, FAQ.class, "faq");

        List<FAQ> faqList = jpaQueryFactory
                .selectFrom(faq)
                .where(builder)
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long countResult = jpaQueryFactory
                .select(faq.count())
                .from(faq)
                .where(builder)
                .fetchOne();

        long totalCount = (countResult != null) ? countResult : 0L;

        List<FAQResponse> responses = faqList.stream()
                .map(f -> FAQResponse.builder()
                        .faqId(f.getId())
                        .questioner(f.getQuestioner())
                        .respondent(f.getRespondent())
                        .isAnswered(f.getIsAnswered())
                        .type(f.getType())
                        .status(f.getStatus())
                        .question(f.getQuestion())
                        .answer(f.getAnswer())
                        .questionAt(f.getQuestionAt().toLocalDate())
                        .answeredAt(f.getAnsweredAt().toLocalDate())
                        .build()
                ).collect(Collectors.toList());

        return SearchResult.<FAQResponse>builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }

    @Transactional
    public void deleteFAQ(CustomUserDetails customUserDetails, List<Long> idList){
        globalService.validateAdmin(customUserDetails);

        faqRepository.deleteAllByIdInBatch(idList);
    }

}
