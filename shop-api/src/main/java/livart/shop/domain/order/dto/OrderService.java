package livart.shop.domain.order.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OrderService {

    public String generateOrderNum() {
        String prefix = "ORD";
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = (int)(Math.random() * 90000) + 10000;
        return prefix + date + random;
    }
}
