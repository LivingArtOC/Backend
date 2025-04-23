package livart.common.domain.setting.repository;

import livart.common.domain.setting.entity.Guide;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GuideRepository extends JpaRepository<Guide, Long> {
    Optional<Guide> findGuideByTitle(String title);
}
