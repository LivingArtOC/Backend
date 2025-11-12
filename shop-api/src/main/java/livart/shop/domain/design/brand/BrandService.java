package livart.shop.domain.design.brand;

import livart.common.domain.design.entity.Brand;
import livart.common.domain.design.repository.BrandRepository;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.shop.domain.design.brand.dto.response.BrandViewResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service("designBrandService")
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public BrandViewResponse get() {
        // 단일 레코드 규약: PK = 1
        Brand brand = brandRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.BRAND_INFO_NOT_FOUND));

        // 이미지 없으면 404
        if (brand.getImageUrl() == null || brand.getImageUrl().isBlank()) {
            throw new CustomException(ErrorCode.BRAND_INFO_NOT_FOUND);
        }

        long updatedAtMillis = toEpochMillis(brand.getUpdatedAt());

        return BrandViewResponse.builder()
                .fileName(brand.getFileName())
                .imageUrl(brand.getImageUrl())
                .updatedAt(updatedAtMillis)   // ETag에 사용할 epoch milli
                .build();
    }

    private long toEpochMillis(LocalDateTime ldt) {
        if (ldt == null) return 0L;
        return ldt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}