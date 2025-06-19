package livart.common.domain.design.repository;

import livart.common.domain.design.entity.Popup;
import livart.common.dto.enums.design.PopupType;
import livart.common.dto.enums.design.PopupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PopupRepository extends JpaRepository<Popup, Long> {
}
