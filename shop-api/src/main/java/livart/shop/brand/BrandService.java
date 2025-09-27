package livart.shop.brand;

import livart.common.domain.design.entity.Brand;
import livart.common.domain.design.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository; //

    public BrandViewResponse get() {
        return brandRepository.findById(1L)
                .map(this::toDto)
                .orElseGet(() -> new BrandViewResponse(null, null, null));
    }

    private BrandViewResponse toDto(Brand b) {
        String alt = (b.getFileName() != null && !b.getFileName().isBlank())
                ? b.getFileName() : "브랜드 소개 이미지";
        String updatedAt = (b.getUpdatedAt() == null) ? null : b.getUpdatedAt().toString();
        return new BrandViewResponse(b.getImageUrl(), alt, updatedAt);
    }
}