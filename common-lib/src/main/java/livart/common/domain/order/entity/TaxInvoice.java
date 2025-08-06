package livart.common.domain.order.entity;

import jakarta.persistence.*;
import livart.common.domain.BaseTime;
import livart.common.dto.enums.conv.TaxStatus;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "tax_invoice")
@Builder
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TaxInvoice extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNum; // 국세청 발행번호
    private LocalDateTime issuedAt;

    @Enumerated(EnumType.STRING)
    private TaxStatus status;

    private String errorMessage;
    private String pdfUrl;

    private String companyName; // 상호명
    private String ownerName; // 대표자명
    private String bizNum; // 사업자 등록번호
    private String senderMail; // 수신할 메일
    private String phoneNum; // 수신할 번호
    private String zipcode; // 우편번호
    private String address; // 제공 주소
    private String detailedAddress; // 상세주소
    private String bizStatus; // 업태
    private String bizType; // 업종


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
