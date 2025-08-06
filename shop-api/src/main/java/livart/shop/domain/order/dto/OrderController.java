package livart.shop.domain.order.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "주문/결제 관련 API")
@RequestMapping("/api/shop/order")
public class OrderController {
    private final OrderService orderService;


}
