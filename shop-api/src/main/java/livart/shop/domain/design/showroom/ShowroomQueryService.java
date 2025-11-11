package livart.shop.domain.design.showroom;

import livart.common.domain.design.entity.InteriorsImage;
import livart.common.domain.design.entity.InteriorsInfo;
import livart.common.domain.design.repository.InteriorsImageRepository;
import livart.common.domain.design.repository.InteriorsInfoRepository;
import livart.common.domain.setting.entity.OperatingHours;
import livart.common.domain.setting.repository.OperatingHoursRepository;
import livart.common.dto.enums.defaultSetting.DayType;
import livart.common.dto.enums.defaultSetting.OperatingHoursType;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.shop.domain.design.showroom.dto.response.ShowroomHoursResponse;
import livart.shop.domain.design.showroom.dto.response.ShowroomInfoResponse;
import livart.shop.domain.design.showroom.dto.response.ShowroomInteriorsResponse;
import livart.shop.domain.design.showroom.dto.response.ShowroomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShowroomQueryService {

    private final InteriorsImageRepository interiorsImageRepository;
    private final InteriorsInfoRepository interiorsInfoRepository;
    private final OperatingHoursRepository operatingHoursRepository;

    /** 전체(정보+사진+운영시간) */
    public ShowroomResponse getShowroom() {
        ShowroomInfoResponse info = getInfo();
        List<ShowroomInteriorsResponse> interiors = getInteriors();
        ShowroomHoursResponse hours = getHours();
        return ShowroomResponse.of(info, interiors, hours);
    }

    /** 사진만 */
    public List<ShowroomInteriorsResponse> getInteriors() {
        List<InteriorsImage> images = interiorsImageRepository.findAll();
        if (images.isEmpty()) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        return images.stream()
                .sorted(Comparator.comparing(InteriorsImage::getOrderIndex,
                        Comparator.nullsLast(Integer::compareTo)))
                .map(ShowroomInteriorsResponse::from)
                .collect(Collectors.toList());
    }

    /** 텍스트/연락처/안내만 (전시장 안내 PK는 1 고정) */
    public ShowroomInfoResponse getInfo() {
        InteriorsInfo info = interiorsInfoRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.INTERIOR_INFO_NOT_FOUND));
        return ShowroomInfoResponse.from(info);
    }

    /** 운영시간만 (INTERIOR_INFO 타입만 조회) */
    public ShowroomHoursResponse getHours() {
        List<OperatingHours> list = operatingHoursRepository
                .findByOperatingHoursType(OperatingHoursType.INTERIOR_INFO);

        if (list.isEmpty()) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        Map<DayType, ShowroomHoursResponse.TimeRange> hours = list.stream()
                .collect(Collectors.toMap(
                        OperatingHours::getDayType,
                        oh -> new ShowroomHoursResponse.TimeRange(oh.getStartTime(), oh.getEndTime()),
                        (a, b) -> a,
                        () -> new EnumMap<>(DayType.class)
                ));
        return new ShowroomHoursResponse(hours);
    }
}