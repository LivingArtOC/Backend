package livart.common.domain.alarm.repository;

import livart.common.domain.alarm.entity.SmsTemplate;
import livart.common.dto.enums.alarm.SmsAutoType;
import livart.common.dto.enums.alarm.SmsType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SmsTemplateRepository extends JpaRepository<SmsTemplate, Long> {
    List<SmsTemplate> findAllByTypeIn(List<SmsType> typeList);
    List<SmsTemplate> findAllBySmsAutoType(SmsAutoType smsAutoType);
}
