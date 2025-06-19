package livart.common.client.biz;

import livart.common.domain.user.repository.BusinessRepository;
import livart.common.dto.request.user.BusinessStatusRequest;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@RequiredArgsConstructor
public class BizCheckClient {

    private final RestTemplate restTemplate;
    private final BusinessRepository businessRepository;

    @Value("${api.biz-check.key}")
    private String serviceKey;

    private static final String API_URL = "https://api.odcloud.kr/api/nts-businessman/v1/status";

    public BizCheckResponse isValidBizNumber(BusinessStatusRequest request) {

        businessRepository.findBusinessByBizRegistrationNum(request.getBizNum())
                .ifPresent(u -> { throw new CustomException(ErrorCode.DUPLICATE_BIZ_NUM); });

        String url = API_URL + "?serviceKey=" + serviceKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject body = new JSONObject();
        body.put("b_no", List.of(request.getBizNum())); // 여러 개도 가능

        HttpEntity<String> http = new HttpEntity<>(body.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                http,
                String.class
        );

        try {
            JSONObject result = new JSONObject(response.getBody());
            JSONArray data = result.getJSONArray("data");

            if (data.length() == 0) {
                throw new CustomException(ErrorCode.INVALID_BIZ_NUMBER);
            }

            JSONObject item = data.getJSONObject(0);
            String status = item.optString("b_stt");

            if (!"계속사업자".equals(status)) {
                throw new CustomException(ErrorCode.INVALID_BIZ_NUMBER);
            }


            String bizStatus = item.optString("b_type");  // 업태
            String bizType = item.optString("b_item");    // 종목

            return BizCheckResponse.builder()
                    .isExist(true)
                    .bizStatus(bizStatus)
                    .bizType(bizType)
                    .bizNum(request.getBizNum())
                    .bizName(request.getBizName())
                    .ownerName(request.getOwnerName())
                    .build();

        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_BIZ_NUMBER);
        }
    }
}
