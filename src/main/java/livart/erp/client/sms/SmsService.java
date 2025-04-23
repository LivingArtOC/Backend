package livart.erp.client.sms;

import livart.common.client.sms.SmsSender;
import livart.common.dto.enums.OtpStatus;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.log.entity.OtpLog;
import livart.common.log.repository.OtpLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final OtpLogRepository otpLogRepository;
    private final SmsSender smsSender;

    public void sendOtp(OtpSendRequest request) {
        String code = generateCode();

        try {
            smsSender.sendSMS(request.getPhoneNum(), "[리바트] 인증번호는 [" + code + "] 입니다.");

            otpLogRepository.save(OtpLog.builder()
                    .phoneNum(request.getPhoneNum())
                    .otpCode(code)
                    .sentAt(Instant.now())
                    .status(OtpStatus.SENT)
                    .build());

        } catch (Exception e) {
            otpLogRepository.save(OtpLog.builder()
                    .phoneNum(request.getPhoneNum())
                    .otpCode(code)
                    .sentAt(Instant.now())
                    .status(OtpStatus.FAILED)
                    .build());
            throw new CustomException(ErrorCode.SMS_SEND_FAILED);
        }
    }

    public void verifyOtp(OtpVerifyRequest request) {
        OtpLog otp = otpLogRepository.findTopByPhoneAndStatusOrderBySentAtDesc(request.getPhoneNum(), OtpStatus.SENT)
                .orElseThrow(() -> new CustomException(ErrorCode.AUTH_CODE_NOT_FOUND));

        if (!otp.getOtpCode().equals(request.getOtpCode())) {
            throw new CustomException(ErrorCode.AUTH_CODE_MISMATCH);
        }

        if (otp.getSentAt().isBefore(Instant.now().minus(3, ChronoUnit.MINUTES))) {
            throw new CustomException(ErrorCode.AUTH_CODE_EXPIRED);
        }

        otp.markUsed();
        otpLogRepository.save(otp);
    }

    private String generateCode() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }
}

