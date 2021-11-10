package com.gs.pi4.api.core.company;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("SELECT c FROM Company c WHERE c.createdBy.id =:userId")
    List<Company> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Company c WHERE c.id !=:companyId")
    List<Company> findAllExceptsCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT c FROM Company c WHERE c.id =:companyId AND c.createdBy.id =:userId")
    Company findByUserIdAndCompanyId(@Param("userId") Long userId, @Param("companyId") Long companyId);
}