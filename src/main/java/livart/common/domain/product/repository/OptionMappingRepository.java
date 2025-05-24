package livart.common.domain.product.repository;

import livart.common.domain.product.entity.Option;
import livart.common.domain.product.entity.OptionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OptionMappingRepository extends JpaRepository<OptionMapping, Long> {
    @Modifying
    @Query("DELETE FROM OptionMapping om WHERE om.option IN :options")
    void deleteByOptionIn(@Param("options") List<Option> options);

}
