package com.gs.pi4.api.core.company;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyPartnerRepository extends JpaRepository<CompanyPartner, Long> {

    @Query("SELECT c FROM CompanyPartner c WHERE (c.fromCompany.id =:fromCompanyId OR c.toCompany.id =:fromCompanyId) AND c.isAccepted = true")
    List<CompanyPartner> findAllPartners(@Param("fromCompanyId") Long fromCompanyId);

    @Query("SELECT c FROM CompanyPartner c WHERE c.fromCompany.id =:fromCompanyId AND c.isAccepted IS NULL")
    List<CompanyPartner> findAllPendingPartners(@Param("fromCompanyId") Long fromCompanyId);

    @Query("SELECT c FROM CompanyPartner c WHERE c.toCompany.id =:toCompanyId AND c.isAccepted IS NULL")
    List<CompanyPartner> findAllPendingRequestsForMe(@Param("toCompanyId") Long toCompanyId);

    @Query("SELECT c FROM CompanyPartner c WHERE c.fromCompany.id =:fromCompanyId AND c.toCompany.id =:toCompanyId AND c.isAccepted = true")
    Boolean isPartner(@Param("fromCompanyId") Long fromCompanyId, @Param("toCompanyId") Long toCompanyId);

    @Query("SELECT c FROM CompanyPartner c WHERE c.fromCompany.id =:fromCompanyId AND c.toCompany.id =:toCompanyId AND c.isAccepted IS NULL")
    CompanyPartner findPartnerRequest(@Param("fromCompanyId") Long fromCompanyId, @Param("toCompanyId") Long toCompanyId);

}