package livart.erp.domain.mileage;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import livart.common.Auth.CustomUserDetails;
import livart.common.domain.mileage.entity.MileageSetting;
import livart.common.domain.mileage.repository.MileageSettingRepository;
import livart.common.domain.user.entity.QUser;
import livart.common.dto.enums.user.MileageType;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.log.entity.MileageLog;
import livart.common.log.entity.QMileageLog;
import livart.common.log.repository.MileageLogRepository;
import livart.common.mapper.SearchResult;
import livart.common.service.GlobalService;
import livart.erp.domain.mileage.dto.request.MileageDefaultDto;
import livart.erp.domain.mileage.dto.request.MileageSearchRequest;
import livart.erp.domain.mileage.dto.request.MileageUsePayDto;
import livart.erp.domain.mileage.dto.response.MileageSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MileageService {
    private final GlobalService globalService;
    private final MileageSettingRepository mileageSettingRepository;
    private final MileageLogRepository mileageLogRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Transactional
    public MileageDefaultDto updateDefault(CustomUserDetails customUserDetails, MileageDefaultDto request){
        globalService.validateAdmin(customUserDetails);

        MileageSetting setting = mileageSettingRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.MILEAGE_SETTING_NOT_FOUND));

        setting.updateDefault(request, customUserDetails.getId());
        return MileageDefaultDto.builder()
                .isExpired(setting.getIsExpired())
                .expireDate(setting.getExpireDate())
                .alarmDate(setting.getAlarmDate())
                .kakaoAlarm(setting.getKakaoAlarm())
                .salePriceStandard(setting.getSalePriceStandard())
                .build();
    }

    public MileageDefaultDto getDefault(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        MileageSetting setting = mileageSettingRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.MILEAGE_SETTING_NOT_FOUND));

        return MileageDefaultDto.builder()
                .isExpired(setting.getIsExpired())
                .expireDate(setting.getExpireDate())
                .alarmDate(setting.getAlarmDate())
                .kakaoAlarm(setting.getKakaoAlarm())
                .salePriceStandard(setting.getSalePriceStandard())
                .build();
    }

    @Transactional
    public MileageUsePayDto updateUsePay(CustomUserDetails customUserDetails, MileageUsePayDto request){
        globalService.validateAdmin(customUserDetails);

        MileageSetting setting = mileageSettingRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.MILEAGE_SETTING_NOT_FOUND));

        setting.updateUsePay(request, customUserDetails.getId());

        return MileageUsePayDto.builder()
                .usableMileageStandard(setting.getUsableMileageStandard())
                .minPurchasePrice(setting.getMinPurchasePrice())
                .isLimited(setting.getIsLimited())
                .maxMileagePercentage(setting.getMaxMileagePercentage())
                .purchaseMileage(setting.getPurchaseMileage())
                .signupMileage(setting.getSignupMileage())
                .paymentRestrict(setting.getPaymentRestrict())
                .build();
    }

    public MileageUsePayDto getUsePay(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        MileageSetting setting = mileageSettingRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.MILEAGE_SETTING_NOT_FOUND));

        return MileageUsePayDto.builder()
                .usableMileageStandard(setting.getUsableMileageStandard())
                .minPurchasePrice(setting.getMinPurchasePrice())
                .isLimited(setting.getIsLimited())
                .maxMileagePercentage(setting.getMaxMileagePercentage())
                .purchaseMileage(setting.getPurchaseMileage())
                .signupMileage(setting.getSignupMileage())
                .paymentRestrict(setting.getPaymentRestrict())
                .build();
    }

    public SearchResult<MileageSearchResponse> searchMileageLog(CustomUserDetails customUserDetails, MileageSearchRequest request, Pageable pageable){
        globalService.validateAdmin(customUserDetails);

        QUser user = QUser.user;
        QMileageLog mileageLog = QMileageLog.mileageLog;
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(request.getKeyword()) && request.getKey() != null){
            switch (request.getKey()){
                case NAME -> builder.and(mileageLog.name.containsIgnoreCase(request.getKeyword()));
                case LOGIN_ID -> builder.and(user.loginId.containsIgnoreCase(request.getKeyword()));
                case AGENT -> builder.and(mileageLog.agent.containsIgnoreCase(request.getKeyword()));
                case ALL -> {
                    BooleanBuilder allBuilder = new BooleanBuilder();
                    allBuilder.or(mileageLog.name.containsIgnoreCase(request.getKeyword()));
                    allBuilder.or(user.loginId.containsIgnoreCase(request.getKeyword()));
                    allBuilder.or(mileageLog.agent.containsIgnoreCase(request.getKeyword()));
                    builder.and(allBuilder);
                }
            }
        }

        if(request.getUseGrantDate() != null){
            if(request.getUseGrantDate().getStartDate() != null){
                builder.and(mileageLog.createdAt.goe(request.getUseGrantDate().getStartDate().atStartOfDay()));
            }
            if(request.getUseGrantDate().getEndDate() != null){
                builder.and(mileageLog.createdAt.loe(request.getUseGrantDate().getEndDate().atTime(23,59,59)));
            }
        }

        if(request.getType() != null && request.getType() != MileageType.ALL){
            builder.and(mileageLog.type.eq(request.getType()));
        }

        List<MileageLog> mileageLogs = jpaQueryFactory
                .selectFrom(mileageLog)
                .leftJoin(mileageLog.user, user).fetchJoin()
                .where(builder)
                .orderBy(mileageLog.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<MileageSearchResponse> responses = mileageLogs.stream()
                .map(m -> MileageSearchResponse.builder()
                        .logId(m.getId())
                        .loginId(m.getUser().getLoginId())
                        .name(m.getName())
                        .type(m.getType())
                        .amount(m.getAmount())
                        .applyDate(m.getCreatedAt().toLocalDate())
                        .agent(m.getAgent())
                        .memo(m.getAdminMemo())
                        .build()
                ).collect(Collectors.toList());

        Long totalCount = Optional.ofNullable(
                jpaQueryFactory.select(mileageLog.count())
                        .from(mileageLog)
                        .where(builder)
                        .fetchOne()
        ).orElse(0L);

        return SearchResult.<MileageSearchResponse> builder()
                .totalCount(totalCount)
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .data(responses)
                .build();
    }
}
