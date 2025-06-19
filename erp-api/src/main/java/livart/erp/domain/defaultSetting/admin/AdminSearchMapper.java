package livart.erp.domain.defaultSetting.admin;

import livart.common.domain.user.entity.Admin;
import livart.common.mapper.BaseMapper;
import livart.erp.domain.defaultSetting.admin.dto.response.AdminSearchResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminSearchMapper extends BaseMapper<Admin, AdminSearchResponse> {
}
