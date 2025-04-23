package livart.common.domain.social.repository;

import livart.common.domain.social.entity.SocialAPI;
import livart.common.dto.enums.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAPIRepository extends JpaRepository<SocialAPI, Long> {

    Optional<SocialAPI> findByProvider(Provider provider);
}
