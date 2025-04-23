package livart.common.client.biz;

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

    @Value("${api.biz-check.key}")
    private String serviceKey;

    private static final String API_URL = "https://api.odcloud.kr/api/nts-businessman/v1/status";

    public boolean isValidBizNumber(String bizNumber) {
        String url = API_URL + "?serviceKey=" + serviceKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JSONObject body = new JSONObject();
        body.put("b_no", List.of(bizNumber)); // 여러 개도 가능

        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );

        try {
            JSONObject result = new JSONObject(response.getBody());
            JSONArray data = result.getJSONArray("data");
            if (data.length() == 0) return false;

            String status = data.getJSONObject(0).getString("b_stt");
            return status.equals("계속사업자"); // 유효한 사업자
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_BIZ_NUMBER);
        }
    }
}
