package livart.common.log.repository;

import livart.common.dto.enums.alarm.SendStatus;
import livart.common.dto.enums.alarm.SmsSendReserveType;
import livart.common.log.entity.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import software.amazon.awssdk.services.ses.endpoints.internal.Value;

import java.time.LocalDateTime;
import java.util.List;

public interface SmsLogRepository extends JpaRepository<SmsLog, Long> {
    List<SmsLog> findByStatusAndSendReserveTypeAndReservedAtLessThanEqualAndRetryCountLessThanEqual(
            SendStatus status,
            SmsSendReserveType reserveType,
            LocalDateTime now,
            Integer count
    );
}
