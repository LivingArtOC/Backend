package livart.erp.domain.defaultSetting.admin;

import livart.common.domain.user.entity.Admin;
import livart.common.mapper.BaseMapper;
import livart.common.dto.request.user.AdminRequest;
import livart.erp.domain.defaultSetting.admin.dto.response.AdminResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AdminRegisterMapper extends BaseMapper<Admin, AdminResponse> {

    Admin toEntity(AdminRequest request);

}
