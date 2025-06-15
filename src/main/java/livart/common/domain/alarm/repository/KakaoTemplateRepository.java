package livart.common.domain.alarm.repository;

import livart.common.domain.alarm.entity.KakaoTemplate;
import livart.common.dto.enums.alarm.KakaoTemplateStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KakaoTemplateRepository extends JpaRepository<KakaoTemplate, Long> {
    List<KakaoTemplate> findAllByStatus(KakaoTemplateStatus status);
}
