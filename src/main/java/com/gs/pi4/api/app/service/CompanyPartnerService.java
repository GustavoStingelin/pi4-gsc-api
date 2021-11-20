package com.gs.pi4.api.app.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.gs.pi4.api.app.vo.company.CompanyPartnerVO;
import com.gs.pi4.api.app.vo.product.ProductVO;
import com.gs.pi4.api.core.company.Company;
import com.gs.pi4.api.core.company.CompanyPartner;
import com.gs.pi4.api.core.company.CompanyPartnerRepository;
import com.gs.pi4.api.core.user.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NonNull;

@Service
public class CompanyPartnerService {

    @Autowired
    CompanyPartnerRepository repository;

    @Autowired
    CompanyService companyService;

    public CompanyPartnerService(CompanyPartnerRepository repository) {
        this.repository = repository;
    }

    public CompanyPartnerVO sendPartnerRequest(@NonNull Company from, @NonNull Company to, @NonNull User user) {
        CompanyPartner partner = CompanyPartner.builder().fromCompany(from).toCompany(to).createdBy(user)
                .createdAt(new Date()).build();

        partner = repository.save(partner);
        return parse2CompanyPartnerVO(partner);
    }

    public boolean isPartner(@NonNull Long fromCompanyId, @NonNull Long toCompanyId) {
        return repository.isPartner(fromCompanyId, toCompanyId) != null;
    }

    public List<CompanyPartnerVO> getPartners(@NonNull Long companyId) {
        List<CompanyPartner> partners = repository.findAllPartners(companyId);
        partners = partners.stream().map(el -> {
             if (el.getToCompany().getId().equals(companyId)) {
                 el.setToCompany(el.getFromCompany());
             }
             return el;
        }).collect(Collectors.toList());

        return parse2CompanyPartnerVO(partners);
    }

    public List<CompanyPartnerVO> findAllExceptsCompanyId(@NonNull Long companyId) {
        List<Company> companies = companyService.findAllExceptsCompanyId(companyId);
        return companyService.parse2CompanyPartnerVO(companies, companyId);
    }

    public List<CompanyPartnerVO> findAllPendingPartners(@NonNull Long companyId) {
        List<CompanyPartner> entities = repository.findAllPendingPartners(companyId);
        return parse2CompanyPartnerVO(entities);
    }

    public List<CompanyPartnerVO> findAllPendingRequestsForMe(@NonNull Long companyId) {
        List<CompanyPartner> entities = repository.findAllPendingRequestsForMe(companyId);
        entities = entities.stream().map(el -> {
            if (el.getToCompany().getId().equals(companyId)) {
                el.setToCompany(el.getFromCompany());
            }
            return el;
       }).collect(Collectors.toList());
       
        return parse2CompanyPartnerVO(entities);
    }

    public void acceptPartnerRequest(@NonNull Long from, @NonNull Long to) {
        CompanyPartner partner = repository.findPartnerRequest(from, to);

        if (partner != null) {
            partner.setAccepted(true);
            repository.save(partner);
        }
    }

    public void declinePartnerRequest(@NonNull Long from, @NonNull Long to) {
        CompanyPartner partner = repository.findPartnerRequest(from, to);

        if (partner != null) {
            partner.setAccepted(false);
            repository.save(partner);
        }
    }

    protected CompanyPartner parse(@NonNull CompanyPartnerVO vo) {
        return CompanyPartner.builder().toCompany(Company.builder().id(vo.getId()).build())
                .isAccepted(vo.isAccepted()).build();
    }

    protected CompanyPartnerVO parse2CompanyPartnerVO(@NonNull CompanyPartner entity) {
        return CompanyPartnerVO.builder().id(entity.getToCompany().getId())
                .name(entity.getToCompany().getName()).isAccepted(entity.isAccepted()).build();
    }

    protected CompanyPartnerVO parse2CompanyPartnerVO(@NonNull Company entity) {
        return CompanyPartnerVO.builder().id(entity.getId()).name(entity.getName())
                .build();
    }

    protected List<CompanyPartnerVO> parse2CompanyPartnerVO(@NonNull List<CompanyPartner> entities) {
        return entities.stream().map(this::parse2CompanyPartnerVO).collect(Collectors.toList());
    }

    public boolean isPartner(CompanyPartnerVO buyer, ProductVO product) {
        return false;
    }

}
