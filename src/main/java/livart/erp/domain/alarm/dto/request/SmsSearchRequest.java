package livart.erp.domain.alarm.dto.request;

import livart.common.dto.enums.alarm.SendStatus;
import livart.common.dto.enums.alarm.SmsDivision;
import livart.common.dto.enums.alarm.SmsForm;
import livart.common.dto.enums.alarm.SmsSendReserveType;
import livart.common.dto.request.DateSearchDto;
import lombok.Getter;

@Getter
public class SmsSearchRequest {
    private SmsSearchKey key;
    private String keyword;
    private SmsForm smsForm;
    private SmsSendReserveType reserveType;
    private DateSearchDto sendAt;
    private SmsDivision smsDivision;
    private SendStatus status;
}
