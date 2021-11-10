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

@Service
public class CompanyService {

    @Autowired
    CompanyRepository repository;

    public Company findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CodeExceptionEnum.COMPANY_NOT_FOUND.toString()));
    }

    public CompanyService(CompanyRepository repository) {
        this.repository = repository;
    }

    public List<Company> findAllExceptsCompanyId(Long companyId) {
        return repository.findAllExceptsCompanyId(companyId);
    }

    public boolean userHasCompany(User user, Long companyId) {
        return repository.findByUserIdAndCompanyId(user.getId(), companyId) != null;
    }

    public CompanyVO createCompany(CompanyVO vo, User user) {
        Company entity = parse2Entity(vo, user);
        entity = repository.save(entity);
        return parse2CompanyVO(entity);
    }

    public Company parse2Entity(CompanyVO vo, User user) {
        return Company.builder().id(vo.getKey()).name(vo.getName()).document(vo.getDocument()).logoImage(vo.getLogo())
                .foundedAt(vo.getFoundedAt()).createdAt(new Date()).createdBy(user).build();
    }

    public CompanyVO parse2CompanyVO(Company entity) {
        return CompanyVO.builder().key(entity.getId()).name(entity.getName()).document(entity.getDocument())
                .logo(entity.getLogoImage()).foundedAt(entity.getFoundedAt()).createdAt(entity.getCreatedAt())
                .changedAt(entity.getChangedAt()).build();
    }

    public List<CompanyVO> parse2CompanyVO(List<Company> entities) {
        return entities.stream().map(this::parse2CompanyVO).collect(Collectors.toList());
    }

    public List<CompanyVO> findAllByUserId(Long userId) {
        List<Company> entities = repository.findAllByUserId(userId);
        return parse2CompanyVO(entities);
    }

    public CompanyPartnerVO parse2CompanyPartnerVO(Company entity, Long fromCompany) {
        return CompanyPartnerVO.builder().toCompanyId(entity.getId()).toCompanyName(entity.getName())
                .isAccepted(entity.getPartners().stream().anyMatch(p -> p.getFromCompany().getId().equals(fromCompany)))
                .build();
    }

    public List<CompanyPartnerVO> parse2CompanyPartnerVO(List<Company> entities, Long fromCompany) {
        return entities.stream().map(el -> parse2CompanyPartnerVO(el, fromCompany)).collect(Collectors.toList());
    }

}
