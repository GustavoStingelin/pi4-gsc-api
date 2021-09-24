package com.gs.pi4.api.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email =:email")
    User findByEmail(@Param("email") String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.email =:email")
    Integer countByEmail(@Param("email") String email);
}
