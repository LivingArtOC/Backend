package livart.erp.domain.defaultSetting.policy;

import livart.common.domain.setting.entity.CompanyInfo;
import livart.common.mapper.BaseMapper;
import livart.erp.domain.defaultSetting.policy.dto.request.CompanyInfoRequest;
import livart.erp.domain.defaultSetting.policy.dto.response.CompanyInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CompanyInfoMapper extends BaseMapper<CompanyInfo, CompanyInfoResponse> {

    CompanyInfo toEntity(CompanyInfoRequest request);

    CompanyInfo updateEntityFromRequest(CompanyInfoRequest request, @MappingTarget CompanyInfo entity);
}
