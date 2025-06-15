package livart.common.domain.alarm.repository;

import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaPackage;
import livart.common.domain.alarm.entity.MailTemplate;
import livart.common.dto.enums.alarm.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MailTemplateRepository extends JpaRepository<MailTemplate, Long> {
    Optional<MailTemplate> findByType(EmailType type);
}
