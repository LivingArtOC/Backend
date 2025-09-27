package livart.shop.faq.dto.request;

import jakarta.validation.constraints.*;

public record FaqSearchRequest(
        String category,              // "ALL" | "주문/결제" | "설치" | ...
        @Min(0) Integer page,
        @Min(1) @Max(100) Integer size
) {
    public String categoryOrAll() { return (category == null || category.isBlank()) ? "ALL" : category; }
    public int pageOrDefault()     { return page == null ? 0  : page; }
    public int sizeOrDefault()     { return size == null ? 10 : size; }
}