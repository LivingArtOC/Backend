package livart.common.domain.promotion.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.CatalogType;
import lombok.*;

@Table(name = "catalogs")
@Builder @Getter @Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Catalog extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CatalogType catalogType;
    private String fileUrl;
    private Long createdBy;
    private Long updatedBy;

    public void update(String fileUrl, Long updatedBy){
        this.fileUrl = fileUrl;
        this.updatedBy = updatedBy;
    }
}
