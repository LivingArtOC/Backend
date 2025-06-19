package livart.erp.domain.defaultSetting.policy;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.setting.entity.CompanyInfo;
import livart.common.domain.setting.entity.OperatingHours;
import livart.common.domain.setting.repository.CompanyInfoRepository;
import livart.common.domain.setting.repository.OperatingHoursRepository;
import livart.common.domain.term.entity.DetailTerm;
import livart.common.domain.term.entity.Term;
import livart.common.domain.term.repository.DetailTermRepository;
import livart.common.domain.term.repository.TermRepository;
import livart.common.dto.enums.defaultSetting.DayType;
import livart.common.dto.enums.defaultSetting.OperatingHoursType;
import livart.common.dto.enums.term.TermSuperType;
import livart.common.dto.enums.term.TermType;
import livart.common.dto.request.CompanyInfoRequest;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.service.GlobalService;
import livart.erp.domain.defaultSetting.policy.dto.request.*;
import livart.erp.domain.defaultSetting.policy.dto.response.CompanyInfoResponse;
import livart.erp.domain.defaultSetting.policy.dto.response.TermsResponse;
import livart.erp.domain.defaultSetting.policy.dto.response.UsePolicyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final GlobalService globalService;
    private final CompanyInfoRepository companyInfoRepository;
    private final TermRepository termRepository;
    private final DetailTermRepository detailTermRepository;
    private final OperatingHoursRepository operatingHoursRepository;

    public CompanyInfoResponse getDefault(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        CompanyInfo companyInfo = companyInfoRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.COMPANY_INFO_NOT_FOUND));

        Map<DayType, CompanyInfoResponse.TimeRange> hours = operatingHoursRepository.findByOperatingHoursType(OperatingHoursType.COMPANY_INFO).stream()
                .collect(Collectors.toMap(
                        OperatingHours::getDayType,
                        bh -> CompanyInfoResponse.TimeRange.builder()
                                .start(bh.getStartTime())
                                .end(bh.getEndTime())
                                .build(),
                        (existing, replacement) -> existing, // 중복 방지
                        () -> new EnumMap<>(DayType.class)
                ));

        return CompanyInfoResponse.builder()
                .companyInfoId(companyInfo.getId())
                .companyName(companyInfo.getCompanyName())
                .bizNum(companyInfo.getBizNum())
                .bizName(companyInfo.getBizName())
                .presidentName(companyInfo.getPresidentName())
                .bizStatus(companyInfo.getBizStatus())
                .bizType(companyInfo.getBizType())
                .email(companyInfo.getEmail())
                .zipcode(companyInfo.getZipcode())
                .address(companyInfo.getAddress())
                .detailAddress(companyInfo.getDetailAddress())
                .phoneNum(companyInfo.getPhoneNum())
                .faxNum(companyInfo.getFaxNum())
                .ecommerceLicense(companyInfo.getEcommerceLicense())
                .companySealURL(companyInfo.getCompanySealURL())
                .hours(hours)
                .build();
    }

    @Transactional
    public CompanyInfoResponse updateDefault(CustomUserDetails customUserDetails, CompanyInfoRequest request){
        globalService.validateAdmin(customUserDetails);

        CompanyInfo companyInfo = companyInfoRepository.findById(1L)
                .map(info -> {
                    info.updateEntityFromRequest(request, customUserDetails.getId());
                    return info;
                })
                .orElseGet(() -> CompanyInfo.builder()
                        .id(1L)
                        .companyName(request.getCompanyName())
                        .bizNum(request.getBizNum())
                        .bizName(request.getBizName())
                        .presidentName(request.getPresidentName())
                        .bizStatus(request.getBizStatus())
                        .bizType(request.getBizType())
                        .email(request.getEmail())
                        .zipcode(request.getZipcode())
                        .address(request.getAddress())
                        .detailAddress(request.getDetailAddress())
                        .phoneNum(request.getPhoneNum())
                        .faxNum(request.getFaxNum())
                        .ecommerceLicense(request.getEcommerceLicense())
                        .companySealURL(request.getCompanySealURL())
                        .updatedBy(customUserDetails.getId())
                        .build());

        CompanyInfo saved = companyInfoRepository.save(companyInfo);

        operatingHoursRepository.deleteByOperatingHoursType(OperatingHoursType.COMPANY_INFO);

        List<OperatingHours> operatingHours = request.getHours().entrySet().stream()
                .map(entry -> {
                    DayType dayType = entry.getKey();
                    CompanyInfoRequest.TimeRange timeRange = entry.getValue();

                    return OperatingHours.builder()
                            .operatingHoursType(OperatingHoursType.COMPANY_INFO)
                            .dayType(dayType)
                            .startTime(timeRange.getStart())
                            .endTime(timeRange.getEnd())
                            .updatedBy(customUserDetails.getId())
                            .build();
                }).collect(Collectors.toList());
        
        Map<DayType, CompanyInfoResponse.TimeRange> hours = operatingHoursRepository.saveAll(operatingHours).stream()
                .collect(Collectors.toMap(
                        OperatingHours::getDayType,
                        bh -> CompanyInfoResponse.TimeRange.builder()
                                .start(bh.getStartTime())
                                .end(bh.getEndTime())
                                .build(),
                        (existing, replacement) -> existing, // 중복 방지
                        () -> new EnumMap<>(DayType.class)
                ));
        
        return CompanyInfoResponse.builder()
                .companyInfoId(saved.getId())
                .companyName(saved.getCompanyName())
                .bizNum(saved.getBizNum())
                .bizName(saved.getBizName())
                .presidentName(saved.getPresidentName())
                .bizStatus(saved.getBizStatus())
                .bizType(saved.getBizType())
                .email(saved.getEmail())
                .zipcode(saved.getZipcode())
                .address(saved.getAddress())
                .detailAddress(saved.getDetailAddress())
                .phoneNum(saved.getPhoneNum())
                .faxNum(saved.getFaxNum())
                .ecommerceLicense(saved.getEcommerceLicense())
                .companySealURL(saved.getCompanySealURL())
                .hours(hours)
                .build();
    }


    public UsePolicyResponse getUsePolicy(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        Term term = termRepository.findByType(TermType.USE_POLICY).orElseThrow(() -> new CustomException(ErrorCode.TERM_NOT_FOUND));
        DetailTerm detailTerm = detailTermRepository.findByTerm(term).orElseThrow(() -> new CustomException(ErrorCode.DETAIL_TERM_NOT_FOUND));

        return UsePolicyResponse.builder()
                .termId(term.getId())
                .isRequired(term.getIsRequired())
                .title(term.getTitle())
                .superType(term.getSuperType())
                .type(term.getType())
                .usePolicyContent(term.getContent())
                .courseContent(detailTerm.getCourse())
                .startDate(detailTerm.getStartDate())
                .endDate(detailTerm.getEndDate())
                .isExposed(detailTerm.getIsExposed())
                .officerEmail(detailTerm.getOfficerEmail())
                .officerPosition(detailTerm.getOfficerPosition())
                .officerName(detailTerm.getOfficerName())
                .officerPhone(detailTerm.getOfficerPhone())
                .build();
    }

    @Transactional
    public UsePolicyResponse updateUsePolicy(CustomUserDetails customUserDetails, UsePolicyRequest request){
        globalService.validateAdmin(customUserDetails);

        Term term = termRepository.findByType(TermType.USE_POLICY)
                .map(t -> {
                    t.update(request.getUsePolicyContent(), customUserDetails.getId());
                    return t;
                })
                .orElseGet(() -> Term.builder()
                        .superType(TermSuperType.CORPORATION)
                        .isRequired(true)
                        .title("이용 약관")
                        .type(TermType.USE_POLICY)
                        .updatedBy(customUserDetails.getId())
                        .content(request.getUsePolicyContent())
                        .build());

        Term saved1 = termRepository.save(term);

        DetailTerm detailTerm = detailTermRepository.findByTerm(saved1)
                .map(d -> {
                    d.updateFromUsePolicy(request.getCourseContent(), request.getStartDate(), request.getEndDate(), request.getIsExposed(),
                            request.getOfficerName(), request.getOfficerPosition(), request.getOfficerPhone(), request.getOfficerEmail(), customUserDetails.getId());
                    return d;
                })
                .orElseGet(() -> DetailTerm.builder()
                        .course(request.getCourseContent())
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .isExposed(request.getIsExposed())
                        .updatedBy(customUserDetails.getId())
                        .officerEmail(request.getOfficerEmail())
                        .officerPhone(request.getOfficerPhone())
                        .officerPosition(request.getOfficerPosition())
                        .officerName(request.getOfficerName())
                        .term(saved1)
                        .build());

        saved1.setDetailTerm(detailTerm);
        Term saved = termRepository.save(saved1);


        return UsePolicyResponse.builder()
                .termId(saved.getId())
                .isRequired(saved.getIsRequired())
                .title(saved.getTitle())
                .superType(saved.getSuperType())
                .type(saved.getType())
                .usePolicyContent(saved.getContent())
                .courseContent(detailTerm.getCourse())
                .startDate(detailTerm.getStartDate())
                .endDate(detailTerm.getEndDate())
                .isExposed(detailTerm.getIsExposed())
                .officerEmail(detailTerm.getOfficerEmail())
                .officerPosition(detailTerm.getOfficerPosition())
                .officerName(detailTerm.getOfficerName())
                .officerPhone(detailTerm.getOfficerPhone())
                .build();
    }

    public List<TermsResponse> getTerm(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        return termRepository.findAllByTypeNotIn(List.of(TermType.USE_POLICY))
                .stream()
                .map(t -> TermsResponse.builder()
                        .termId(t.getId())
                        .superType(t.getSuperType())
                        .type(t.getType())
                        .required(t.getIsRequired())
                        .title(t.getTitle())
                        .content(t.getContent())
                        .build())
                .collect(Collectors.toList());

    }

    public TermsResponse updateTerm(CustomUserDetails customUserDetails, TermsRequest request){
        globalService.validateAdmin(customUserDetails);

        if(request.getType() == null){
            throw new CustomException(ErrorCode.INVALID_TYPE);
        }

        Term term = termRepository.findByTypeAndIsRequired(request.getType(), request.getIsRequired())
                .map(t -> {
                    t.updateOthers(request.getTitle(), request.getContent(), customUserDetails.getId());
                    return t;
                })
                .orElseGet(() -> Term.builder()
                        .superType(request.getSuperType())
                        .isRequired(request.getIsRequired())
                        .title(request.getTitle())
                        .type(request.getType())
                        .updatedBy(customUserDetails.getId())
                        .content(request.getContent())
                        .build());

        Term saved = termRepository.save(term);

        return TermsResponse.builder()
                .termId(saved.getId())
                .superType(saved.getSuperType())
                .type(saved.getType())
                .required(saved.getIsRequired())
                .title(saved.getTitle())
                .content(saved.getContent())
                .build();
    }
}
