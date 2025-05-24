package livart.common.domain.product.repository;

import livart.common.domain.product.entity.DetailedOption;
import livart.common.domain.product.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface DetailedOptionRepository extends JpaRepository<DetailedOption, Long> {
    List<DetailedOption> findByProductIdOrderByOrderIndexAsc(Long productId);

    @Modifying
    void deleteAllByProductId(Long productId);
}
