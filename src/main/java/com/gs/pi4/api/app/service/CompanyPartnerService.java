package com.gs.pi4.api.app.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.gs.pi4.api.app.vo.company.CompanyPartnerVO;
import com.gs.pi4.api.core.company.Company;
import com.gs.pi4.api.core.company.CompanyPartner;
import com.gs.pi4.api.core.company.CompanyPartnerRepository;
import com.gs.pi4.api.core.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyPartnerService {

    @Autowired
    CompanyPartnerRepository repository;

    @Autowired
    CompanyService companyService;

    public CompanyPartnerService(CompanyPartnerRepository repository) {
        this.repository = repository;
    }

    public CompanyPartnerVO sendPartnerRequest(Company from, Company to, User user) {
        CompanyPartner partner = CompanyPartner.builder().fromCompany(from).toCompany(to).createdBy(user)
                .createdAt(new Date()).build();

        partner = repository.save(partner);
        return parse2CompanyPartnerVO(partner);
    }

    public Boolean isPartner(Long fromCompanyId, Long toCompanyId) {
        return repository.isPartner(fromCompanyId, toCompanyId) != null;
    }

    public List<CompanyPartnerVO> getPartners(Long companyId) {
        List<CompanyPartner> partners = repository.findAllPartners(companyId);
        partners = partners.stream().map(el -> {
             if (el.getToCompany().getId().equals(companyId)) {
                 el.setToCompany(el.getFromCompany());
             }
             return el;
        }).collect(Collectors.toList());

        return parse2CompanyPartnerVO(partners);
    }

    public List<CompanyPartnerVO> findAllExceptsCompanyId(Long companyId) {
        List<Company> companies = companyService.findAllExceptsCompanyId(companyId);
        return companyService.parse2CompanyPartnerVO(companies, companyId);
    }

    public List<CompanyPartnerVO> findAllPendingPartners(Long companyId) {
        List<CompanyPartner> entities = repository.findAllPendingPartners(companyId);
        return parse2CompanyPartnerVO(entities);
    }

    public List<CompanyPartnerVO> findAllPendingRequestsForMe(Long companyId) {
        List<CompanyPartner> entities = repository.findAllPendingRequestsForMe(companyId);
        entities = entities.stream().map(el -> {
            if (el.getToCompany().getId().equals(companyId)) {
                el.setToCompany(el.getFromCompany());
            }
            return el;
       }).collect(Collectors.toList());
       
        return parse2CompanyPartnerVO(entities);
    }

    public void acceptPartnerRequest(Long from, Long to) {
        CompanyPartner partner = repository.findPartnerRequest(from, to);

        if (partner != null) {
            partner.setIsAccepted(true);
            repository.save(partner);
        }
    }

    public void declinePartnerRequest(Long from, Long to) {
        CompanyPartner partner = repository.findPartnerRequest(from, to);

        if (partner != null) {
            partner.setIsAccepted(false);
            repository.save(partner);
        }
    }

    public CompanyPartner parse(CompanyPartnerVO vo) {
        return CompanyPartner.builder().toCompany(Company.builder().id(vo.getToCompanyId()).build())
                .isAccepted(vo.getIsAccepted()).build();
    }

    public CompanyPartnerVO parse2CompanyPartnerVO(CompanyPartner entity) {
        return CompanyPartnerVO.builder().toCompanyId(entity.getToCompany().getId())
                .toCompanyName(entity.getToCompany().getName()).isAccepted(entity.getIsAccepted()).build();
    }

    public CompanyPartnerVO parse2CompanyPartnerVO(Company entity) {
        return CompanyPartnerVO.builder().toCompanyId(entity.getId()).toCompanyName(entity.getName())
                .build();
    }

    public List<CompanyPartnerVO> parse2CompanyPartnerVO(List<CompanyPartner> entities) {
        return entities.stream().map(this::parse2CompanyPartnerVO).collect(Collectors.toList());
    }

}
