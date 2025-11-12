package livart.shop.domain.design.showroom;

import io.swagger.v3.oas.annotations.tags.Tag;
import livart.shop.domain.design.showroom.dto.response.ShowroomHoursResponse;
import livart.shop.domain.design.showroom.dto.response.ShowroomInfoResponse;
import livart.shop.domain.design.showroom.dto.response.ShowroomInteriorsResponse;
import livart.shop.domain.design.showroom.dto.response.ShowroomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "전시장 소개 API")
@RequestMapping("/api/showroom")
public class ShowroomController {

    private final ShowroomQueryService showroomQueryService;

    /** 전체(정보+사진+운영시간) */
    @GetMapping
    public ShowroomResponse getShowroom() {
        return showroomQueryService.getShowroom();
    }

    /** 사진만 */
    @GetMapping("/interiors")
    public List<ShowroomInteriorsResponse> getInteriors() {
        return showroomQueryService.getInteriors();
    }

    /** 텍스트/연락처/안내만 */
    @GetMapping("/info")
    public ShowroomInfoResponse getInfo() {
        return showroomQueryService.getInfo();
    }

    /** 운영시간만 */
    @GetMapping("/hours")
    public ShowroomHoursResponse getHours() {
        return showroomQueryService.getHours();
    }
}

