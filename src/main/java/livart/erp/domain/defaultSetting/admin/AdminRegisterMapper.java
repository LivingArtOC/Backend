package livart.erp.domain.defaultSetting.admin;

import livart.common.domain.user.entity.Admin;
import livart.common.mapper.BaseMapper;
import livart.erp.domain.defaultSetting.admin.dto.request.AdminRequest;
import livart.erp.domain.defaultSetting.admin.dto.response.AdminResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AdminRegisterMapper extends BaseMapper<Admin, AdminResponse> {

    Admin toEntity(AdminRequest request);

}
