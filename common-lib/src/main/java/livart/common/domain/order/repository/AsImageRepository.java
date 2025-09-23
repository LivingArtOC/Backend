package livart.common.domain.order.repository;

import livart.common.domain.order.entity.ASImage;
import livart.common.domain.order.entity.AfterServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsImageRepository extends JpaRepository<ASImage, Long> {
    List<ASImage> findASImagesByAfterServiceRequest(AfterServiceRequest request);
}
