package livart.erp.client.sms;

import lombok.Getter;

@Getter
public class OtpVerifyRequest {
    private String phoneNum;
    private String otpCode;
}