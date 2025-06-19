package livart.shop.security.dto.request;

import lombok.Getter;
import software.amazon.awssdk.services.ses.endpoints.internal.Value;

@Getter
public class LoginIdFindRequest {
    private String name;
    private String phoneNum;
}
