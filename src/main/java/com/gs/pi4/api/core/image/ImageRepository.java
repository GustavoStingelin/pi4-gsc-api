package com.gs.pi4.api.core.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("SELECT i FROM Image i WHERE i.id =:id AND i.externalId =:key")
    Image findByIdAndKey(@Param("id") Long id, @Param("key") String key);

}
