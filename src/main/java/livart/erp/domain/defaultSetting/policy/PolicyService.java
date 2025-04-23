package livart.erp.domain.defaultSetting.policy;

import livart.common.Auth.CustomUserDetails;
import livart.common.domain.setting.entity.CompanyInfo;
import livart.common.domain.setting.repository.CompanyInfoRepository;
import livart.common.domain.term.entity.DetailTerms;
import livart.common.domain.term.entity.Terms;
import livart.common.domain.term.repository.DetailTermsRepository;
import livart.common.domain.term.repository.TermsRepository;
import livart.common.dto.enums.Required;
import livart.common.dto.enums.TermType;
import livart.common.exception.CustomException;
import livart.common.exception.ErrorCode;
import livart.common.service.GlobalService;
import livart.erp.domain.defaultSetting.policy.dto.request.CompanyInfoRequest;
import livart.erp.domain.defaultSetting.policy.dto.request.CourseRequest;
import livart.erp.domain.defaultSetting.policy.dto.request.TermsRequest;
import livart.erp.domain.defaultSetting.policy.dto.request.UsePolicyRequest;
import livart.erp.domain.defaultSetting.policy.dto.response.CompanyInfoResponse;
import livart.erp.domain.defaultSetting.policy.dto.response.CourseResponse;
import livart.erp.domain.defaultSetting.policy.dto.response.TermsResponse;
import livart.erp.domain.defaultSetting.policy.dto.response.UsePolicyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final GlobalService globalService;
    private final CompanyInfoRepository companyInfoRepository;
    private final CompanyInfoMapper companyInfoMapper;
    private final TermsRepository termsRepository;
    private final DetailTermsRepository detailTermsRepository;

    @Transactional
    public CompanyInfoResponse saveDefault(CustomUserDetails customUserDetails, CompanyInfoRequest request){
        globalService.validateAdmin(customUserDetails);

        CompanyInfo companyInfo = companyInfoMapper.toEntity(request).toBuilder()
                .id(1L)
                .updatedBy(customUserDetails.getId())
                .build();
        CompanyInfo saved = companyInfoRepository.save(companyInfo);

        return companyInfoMapper.toDto(saved);
    }

    public CompanyInfoResponse getDefault(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        CompanyInfo companyInfo = companyInfoRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        return companyInfoMapper.toDto(companyInfo);
    }

    @Transactional
    public CompanyInfoResponse updateDefault(CustomUserDetails customUserDetails, CompanyInfoRequest request){
        globalService.validateAdmin(customUserDetails);

        CompanyInfo companyInfo = companyInfoRepository.findById(1L)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        companyInfoMapper.updateEntityFromRequest(request, companyInfo);
        companyInfo.setUpdatedBy(customUserDetails.getId());
        CompanyInfo saved = companyInfoRepository.save(companyInfo);

        return companyInfoMapper.toDto(saved);
    }

    @Transactional
    public UsePolicyResponse saveUsePolicy(CustomUserDetails customUserDetails, UsePolicyRequest request){
        globalService.validateAdmin(customUserDetails);

        Terms terms = Terms.builder()
                .isRequired(Required.REQUIRED)
                .title("이용 약관")
                .type(TermType.USE)
                .content(request.getContent())
                .build();

        Terms savedTerm = termsRepository.save(terms);

        DetailTerms detailTerm = DetailTerms.builder()
                .course(request.getCourse())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isExposed(request.getIsExposed())
                .updatedBy(customUserDetails.getId())
                .terms(savedTerm)
                .build();

        DetailTerms savedDetail = detailTermsRepository.save(detailTerm);

        return UsePolicyResponse.builder()
                .title(savedTerm.getTitle())
                .content(savedTerm.getContent())
                .course(savedDetail.getCourse())
                .startDate(savedDetail.getStartDate())
                .endDate(savedDetail.getEndDate())
                .isExposed(savedDetail.getIsExposed())
                .build();
    }

    public UsePolicyResponse getUsePolicy(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        Terms terms = termsRepository.findByTitle("이용 약관").orElseThrow(() -> new CustomException(ErrorCode.TERM_NOT_FOUND));
        DetailTerms detailTerms = detailTermsRepository.findByTerms(terms).orElseThrow(() -> new CustomException(ErrorCode.DETAIL_TERM_NOT_FOUND));

        return UsePolicyResponse.builder()
                .title(terms.getTitle())
                .content(terms.getContent())
                .course(detailTerms.getCourse())
                .startDate(detailTerms.getStartDate())
                .endDate(detailTerms.getEndDate())
                .isExposed(detailTerms.getIsExposed())
                .build();
    }

    @Transactional
    public UsePolicyResponse updateUsePolicy(CustomUserDetails customUserDetails, UsePolicyRequest request){
        globalService.validateAdmin(customUserDetails);

        Terms terms = termsRepository.findByTitle("이용 약관").orElseThrow(() -> new CustomException(ErrorCode.TERM_NOT_FOUND));
        DetailTerms detailTerms = detailTermsRepository.findByTerms(terms).orElseThrow(() -> new CustomException(ErrorCode.DETAIL_TERM_NOT_FOUND));

        terms.update(request.getContent());
        Terms savedTerm = termsRepository.save(terms);

        detailTerms.updateFromUsePolicy(request.getCourse(), request.getStartDate(), request.getEndDate(), request.getIsExposed(), customUserDetails.getId());
        DetailTerms savedDetail = detailTermsRepository.save(detailTerms);

        return UsePolicyResponse.builder()
                .title(savedTerm.getTitle())
                .content(savedTerm.getContent())
                .course(savedDetail.getCourse())
                .startDate(savedDetail.getStartDate())
                .endDate(savedDetail.getEndDate())
                .isExposed(savedDetail.getIsExposed())
                .build();
    }

    @Transactional
    public CourseResponse saveCourse(CustomUserDetails customUserDetails, CourseRequest request){
        globalService.validateAdmin(customUserDetails);

        Terms terms = Terms.builder()
                .isRequired(Required.REQUIRED)
                .title("개인정보 처리방침")
                .type(TermType.COURSE)
                .content(request.getContent())
                .build();

        Terms savedTerm = termsRepository.save(terms);

        DetailTerms detailTerm = DetailTerms.builder()
                .officerName(request.getOfficerName())
                .officerPosition(request.getOfficerPosition())
                .officerEmail(request.getOfficerEmail())
                .officerPhone(request.getOfficerPhone())
                .updatedBy(customUserDetails.getId())
                .terms(savedTerm)
                .build();

        DetailTerms savedDetail = detailTermsRepository.save(detailTerm);

        return CourseResponse.builder()
                .title(savedTerm.getTitle())
                .content(savedTerm.getContent())
                .officerName(savedDetail.getOfficerName())
                .officerPosition(savedDetail.getOfficerPosition())
                .officerEmail(savedDetail.getOfficerEmail())
                .officerPhone(savedDetail.getOfficerPhone())
                .build();
    }

    public CourseResponse getCourse(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        Terms terms = termsRepository.findByTitle("개인정보 처리방침").orElseThrow(() -> new CustomException(ErrorCode.TERM_NOT_FOUND));
        DetailTerms detailTerms = detailTermsRepository.findByTerms(terms).orElseThrow(() -> new CustomException(ErrorCode.DETAIL_TERM_NOT_FOUND));

        return CourseResponse.builder()
                .title(terms.getTitle())
                .content(terms.getContent())
                .officerName(detailTerms.getOfficerName())
                .officerPosition(detailTerms.getOfficerPosition())
                .officerEmail(detailTerms.getOfficerEmail())
                .officerPhone(detailTerms.getOfficerPhone())
                .build();
    }

    @Transactional
    public CourseResponse updateCourse(CustomUserDetails customUserDetails, CourseRequest request){
        globalService.validateAdmin(customUserDetails);

        Terms terms = termsRepository.findByTitle("개인정보 처리방침").orElseThrow(() -> new CustomException(ErrorCode.TERM_NOT_FOUND));
        DetailTerms detailTerms = detailTermsRepository.findByTerms(terms).orElseThrow(() -> new CustomException(ErrorCode.DETAIL_TERM_NOT_FOUND));

        terms.update(request.getContent());
        Terms savedTerm = termsRepository.save(terms);

        detailTerms.updateFromCourse(request.getOfficerName(),request.getOfficerPosition(),request.getOfficerPhone(), request.getOfficerEmail(),  customUserDetails.getId());
        DetailTerms savedDetail = detailTermsRepository.save(detailTerms);

        return CourseResponse.builder()
                .title(savedTerm.getTitle())
                .content(savedTerm.getContent())
                .officerName(savedDetail.getOfficerName())
                .officerPosition(savedDetail.getOfficerPosition())
                .officerEmail(savedDetail.getOfficerEmail())
                .officerPhone(savedDetail.getOfficerPhone())
                .build();
    }

    public List<TermsResponse> getTerms(CustomUserDetails customUserDetails){
        globalService.validateAdmin(customUserDetails);

        List<TermsResponse> termsResponseList = termsRepository.findAllByTypeNotIn(List.of(TermType.USE, TermType.COURSE))
                .stream()
                .map(terms -> TermsResponse.builder()
                        .termId(terms.getId())
                        .required(terms.getIsRequired())
                        .title(terms.getTitle())
                        .content(terms.getContent())
                        .build())
                .collect(Collectors.toList());

        return termsResponseList;
    }

    public TermsResponse updateTerm(CustomUserDetails customUserDetails, TermsRequest request, Long termId){
        globalService.validateAdmin(customUserDetails);

        Terms term = termsRepository.findById(termId).orElseThrow(() -> new CustomException(ErrorCode.TERM_NOT_FOUND));

        term.update(request.getContent());
        Terms saved = termsRepository.save(term);
        return TermsResponse.builder()
                .termId(saved.getId())
                .required(saved.getIsRequired())
                .title(saved.getTitle())
                .content(saved.getContent())
                .build();
    }
}
