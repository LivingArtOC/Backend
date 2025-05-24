package livart.common.Auth.repository;

import livart.common.Auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Long userId);
    void deleteByUserId(Long userId);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
