package livart.shop.faq;

import livart.common.domain.support.faq.entity.FAQ;
import livart.common.domain.support.faq.repository.FAQRepository;
import livart.common.dto.enums.as.FAQStatus;
import livart.common.dto.enums.as.QuestionType;
import livart.shop.faq.dto.request.FaqSearchRequest;
import livart.shop.faq.dto.response.FaqPageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FaqService {


    private final FAQRepository faqRepository;

    public FaqPageResponse list(FaqSearchRequest req) {
        int page = req.pageOrDefault();
        int size = req.sizeOrDefault();

        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt")
                .and(Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<FAQ> pageData;
        if (isAll(req.categoryOrAll())) {
            // Example 조회: status=REGISTER 인 것만
            FAQ probe = FAQ.builder().status(FAQStatus.REGISTER).build();
            pageData = faqRepository.findAll(Example.of(probe, ExampleMatcher.matchingAll()), pageable);
        } else {
            QuestionType type = toType(req.categoryOrAll());
            FAQ probe = FAQ.builder().status(FAQStatus.REGISTER).type(type).build();
            pageData = faqRepository.findAll(Example.of(probe, ExampleMatcher.matchingAll()), pageable);
        }

        return FaqPageResponse.from(pageData);
    }

    /** 카테고리(ALL/개별) 기준 최신 1건의 시간으로 ETag 생성 */
    public String generateEtag(String categoryOrAll) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updatedAt")
                .and(Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable firstOne = PageRequest.of(0, 1, sort);

        Page<FAQ> page;
        if (isAll(categoryOrAll)) {
            FAQ probe = FAQ.builder().status(FAQStatus.REGISTER).build();
            page = faqRepository.findAll(Example.of(probe, ExampleMatcher.matchingAll()), firstOne);
        } else {
            QuestionType type = toType(categoryOrAll);
            FAQ probe = FAQ.builder().status(FAQStatus.REGISTER).type(type).build();
            page = faqRepository.findAll(Example.of(probe, ExampleMatcher.matchingAll()), firstOne);
        }

        LocalDateTime last = page.getContent().isEmpty()
                ? null
                : Optional.ofNullable(page.getContent().get(0).getUpdatedAt())
                .orElse(page.getContent().get(0).getCreatedAt());

        String raw = (categoryOrAll == null ? "ALL" : categoryOrAll) + "|" + (last == null ? 0 : last.hashCode());
        return "\"" + raw.hashCode() + "\"";
    }

    private boolean isAll(String category) {
        return category == null || category.isBlank() || "ALL".equalsIgnoreCase(category.trim());
    }

    /** ERP 분류명 ↔ enum 매핑 (간단 매핑) */
    private QuestionType toType(String name) {
        String n = name.replace(" ", "").replace("-", "").replace("_","").toUpperCase();
        return switch (n) {
            case "주문/결제", "주문결제", "PAYORDER" -> QuestionType.PAY_ORDER;
            case "설치", "INSTALL" -> QuestionType.INSTALL;
            case "A/S", "AS" -> QuestionType.AS;
            case "배송/환불", "배송환불", "DELIVERYREFUND" -> QuestionType.DELIVERY_REFUND;
            case "서비스이용", "SITEUSAGE" -> QuestionType.SITE_USAGE;
            case "제품", "PRODUCT" -> QuestionType.PRODUCT;
            default -> QuestionType.OTHERS;
        };
    }
}