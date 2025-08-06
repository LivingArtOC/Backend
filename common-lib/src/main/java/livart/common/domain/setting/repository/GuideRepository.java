package livart.common.domain.setting.repository;

import livart.common.domain.setting.entity.Guide;
import livart.common.dto.enums.defaultSetting.GuideType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GuideRepository extends JpaRepository<Guide, Long> {
    Optional<Guide> findGuideByType(GuideType type);

    @EntityGraph(attributePaths = "guideImages")
    List<Guide> findGuideByTypeIn(List<GuideType> types);
}
