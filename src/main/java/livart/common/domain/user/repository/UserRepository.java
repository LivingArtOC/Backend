package livart.common.domain.user.repository;

import livart.common.domain.user.entity.User;
import livart.common.dto.enums.user.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(String loginId);
    Optional<User> findBySocialIdAndProvider(String socialId, Provider provider);
    boolean existsByLoginId(String loginId);

}
