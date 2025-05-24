package livart.erp.domain.promotion;

import livart.common.domain.promotion.entity.Coupon;
import livart.common.domain.user.entity.Admin;
import livart.common.mapper.BaseMapper;
import livart.erp.domain.defaultSetting.admin.dto.request.AdminRequest;
import livart.erp.domain.defaultSetting.admin.dto.response.AdminResponse;
import livart.erp.domain.promotion.dto.request.CouponRegisterRequest;
import livart.erp.domain.promotion.dto.response.CouponRegisterResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
public interface CouponRegisterMapper extends BaseMapper<Coupon, CouponRegisterResponse> {

    Coupon toEntity(CouponRegisterRequest request);
}
