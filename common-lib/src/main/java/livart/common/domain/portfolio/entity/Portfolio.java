package livart.common.domain.portfolio.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.domain.order.entity.Order;
import livart.common.dto.enums.portfolio.PortfolioStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "portfolio")
@Entity @Builder @Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Portfolio extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String companyName;
    private String location;
    private String concept;

    @Enumerated(EnumType.STRING)
    private PortfolioStatus status;

    @Lob
    private String description;
    private LocalDate registerStartDate;
    private LocalDate registerEndDate;
    private Long createdBy;
    private Long updatedBy;

    @Builder.Default
    private Boolean isPinned = false;

    @Builder.Default
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioImage> portfolioImages = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioItem> portfolioItems = new ArrayList<>();

    @Setter
    @Builder.Default
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PortfolioDisplay> portfolioDisplays = new ArrayList<>();

    public void update(String companyName, String location, String concept, String description, LocalDate registerStartDate, LocalDate registerEndDate, Long updatedBy){
        this.companyName = companyName;
        this.location = location;
        this.concept = concept;
        this.description = description;
        this.registerStartDate = registerStartDate;
        this.registerEndDate = registerEndDate;
        this.updatedBy = updatedBy;
    }

    public void updateStatus(PortfolioStatus status, Long updatedBy){
        this.status = status;
        this.updatedBy = updatedBy;
    }

    public void updateIsPinned(Boolean updatePin, Long updatedBy){
        this.isPinned = updatePin;
        this.updatedBy = updatedBy;
    }
}
