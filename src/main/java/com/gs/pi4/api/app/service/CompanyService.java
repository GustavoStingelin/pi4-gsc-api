package com.gs.pi4.api.app.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.gs.pi4.api.api.exception.CodeExceptionEnum;
import com.gs.pi4.api.api.exception.ResourceNotFoundException;
import com.gs.pi4.api.app.vo.company.CompanyPartnerVO;
import com.gs.pi4.api.app.vo.company.CompanyVO;
import com.gs.pi4.api.core.company.Company;
import com.gs.pi4.api.core.company.CompanyRepository;
import com.gs.pi4.api.core.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class CompanyService {

    @Autowired
    CompanyRepository repository;

    public Company findById(@NonNull Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CodeExceptionEnum.COMPANY_NOT_FOUND.toString()));
    }

    public CompanyService(@NonNull CompanyRepository repository) {
        this.repository = repository;
    }

    public List<Company> findAllExceptsCompanyId(@NonNull Long companyId) {
        return repository.findAllExceptsCompanyId(companyId);
    }

    public boolean userHasCompany(@NonNull User user, @NonNull Long companyId) {
        return repository.findByUserIdAndCompanyId(user.getId(), companyId) != null;
    }

    public CompanyVO createCompany(@NonNull CompanyVO vo, @NonNull User user) {
        Company entity = parse2Entity(vo, user);
        entity = repository.save(entity);
        return parse2CompanyVO(entity);
    }

    protected Company parse2Entity(@NonNull CompanyVO vo, @NonNull User user) {
        return Company.builder().id(vo.getKey()).name(vo.getName()).document(vo.getDocument()).logoImage(vo.getLogo())
                .foundedAt(vo.getFoundedAt()).createdAt(new Date()).createdBy(user).build();
    }

    protected CompanyVO parse2CompanyVO(@NonNull Company entity) {
        return CompanyVO.builder().key(entity.getId()).name(entity.getName()).document(entity.getDocument())
                .logo(entity.getLogoImage()).foundedAt(entity.getFoundedAt()).createdAt(entity.getCreatedAt())
                .changedAt(entity.getChangedAt()).build();
    }

    protected List<CompanyVO> parse2CompanyVO(@NonNull List<Company> entities) {
        return entities.stream().map(this::parse2CompanyVO).collect(Collectors.toList());
    }

    public List<CompanyVO> findAllByUserId(@NonNull Long userId) {
        List<Company> entities = repository.findAllByUserId(userId);
        return parse2CompanyVO(entities);
    }

    protected CompanyPartnerVO parse2CompanyPartnerVO(@NonNull Company entity, @NonNull Long fromCompany) {
        return CompanyPartnerVO.builder().id(entity.getId()).name(entity.getName())
                .isAccepted(entity.getPartners().stream().anyMatch(p -> p.getFromCompany().getId().equals(fromCompany)))
                .build();
    }

    protected List<CompanyPartnerVO> parse2CompanyPartnerVO(@NonNull List<Company> entities, @NonNull Long fromCompany) {
        return entities.stream().map(el -> parse2CompanyPartnerVO(el, fromCompany)).collect(Collectors.toList());
    }

}
