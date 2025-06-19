package livart.erp.domain.promotion;

import livart.common.domain.promotion.entity.Coupon;
import livart.common.mapper.BaseMapper;
import livart.common.dto.request.CouponRegisterRequest;
import livart.erp.domain.promotion.dto.response.CouponRegisterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface CouponRegisterMapper extends BaseMapper<Coupon, CouponRegisterResponse> {

    Coupon toEntity(CouponRegisterRequest request);
}
