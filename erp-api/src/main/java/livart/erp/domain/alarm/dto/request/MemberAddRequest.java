package livart.erp.domain.alarm.dto.request;

import livart.common.dto.enums.alarm.AlarmType;
import livart.common.dto.enums.alarm.Status;
import livart.common.dto.enums.user.Provider;
import livart.common.dto.enums.user.Role;
import livart.common.dto.enums.user.UserStatus;
import livart.common.dto.request.DateSearchDto;
import livart.erp.domain.member.dto.request.IntegerRangeSearch;
import livart.erp.domain.member.dto.request.SearchKey;
import lombok.Getter;

@Getter
public class MemberAddRequest {
    private SearchKey key;
    private String keyword;
    private AlarmType alarmType; // 수신 동의 여부 용도
    private Role role;
    private Status status;
    private DateSearchDto signUpDate;
    private DateSearchDto lastLoginDate;
    private IntegerRangeSearch mileage;
    private IntegerRangeSearch orderCount;
    private Boolean emailNotice; // 메일 수신 동의 여부
    private Boolean smsNotice; // SMS 수신 동의 여부
    private Provider provider;
}
