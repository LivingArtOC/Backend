package livart.common.log.repository;

import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaPackage;
import livart.common.dto.enums.OtpStatus;
import livart.common.log.entity.OtpLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpLogRepository extends JpaRepository<OtpLog,Long> {
    Optional<OtpLog> findTopByPhoneNumAndStatusOrderBySentAtDesc(String phoneNum, OtpStatus status);
}
