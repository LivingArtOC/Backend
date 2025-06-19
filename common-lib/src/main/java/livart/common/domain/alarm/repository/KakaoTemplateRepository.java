package livart.common.domain.alarm.repository;

import livart.common.domain.alarm.entity.KakaoTemplate;
import livart.common.dto.enums.alarm.KakaoTemplateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface KakaoTemplateRepository extends JpaRepository<KakaoTemplate, Long> {
    List<KakaoTemplate> findAllByStatus(KakaoTemplateStatus status);
}
