package livart.shop.domain.design.faq;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import livart.common.domain.support.faq.entity.FAQ;
import livart.common.domain.support.faq.entity.QFAQ;
import livart.common.dto.enums.as.FAQStatus;
import livart.common.dto.enums.as.QuestionType;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.shop.domain.design.faq.dto.response.FaqResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service("designFaqService")
@Transactional(readOnly = true)
public class FaqService {

    private final JPAQueryFactory jpaQueryFactory;

    private QuestionType parseTypeOrNull(String type) {
        if (type == null || type.isBlank() || "ALL".equalsIgnoreCase(type)) {
            return null;
        }
        try {
            return QuestionType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_TYPE);
        }
    }

    public List<FaqResponse> getFaqs(String type) {
        return getFaqs(parseTypeOrNull(type));
    }

    public List<FaqResponse> getFaqs(QuestionType type) {
        QFAQ faq = QFAQ.fAQ;

        BooleanBuilder where = new BooleanBuilder()
                .and(faq.status.eq(FAQStatus.REGISTER))
                .and(faq.isAnswered.isTrue());

        if (type != null) {
            where.and(faq.type.eq(type));
        }

        List<FAQ> list = jpaQueryFactory
                .selectFrom(faq)
                .where(where)
                .orderBy(faq.questionAt.desc())
                .fetch();

        if (list == null || list.isEmpty()) {
            throw new CustomException(ErrorCode.FAQ_NOT_FOUND);
        }

        return list.stream()
                .map(FaqResponse::from)
                .collect(Collectors.toList());
    }
}