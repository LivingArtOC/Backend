package livart.common.domain.design.repository;

import livart.common.domain.design.entity.Popup;
import livart.common.dto.enums.PopupType;
import livart.common.dto.enums.PopupStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface PopupRepository extends JpaRepository<Popup, Long> {
    @Query("""
    SELECT a FROM Popup a
    WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :title, '%'))
      AND a.status = :staus
      AND a.popupType = :popupType
      AND (
           a.exposedStartDate BETWEEN :start AND :end
           OR a.exposedEndDate BETWEEN :start AND :end
      )
""")
    List<Popup> findPopupsByTitleAndTypeAndExposure(
            @Param("title") String title,
            @Param("status") PopupStatus status,
            @Param("popupType") PopupType popupType,
            @Param("start") Instant start,
            @Param("end") Instant end
    );
}
