package com.cabbooking.repository;

import com.cabbooking.entity.Driver;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepository extends JpaRepository<Driver, String> {
    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    Optional<Driver> findByDriverId(String id);

    Optional<Driver> findByEmail(String email);

    @Query("SELECT d FROM Driver d WHERE d.isAvailable = true AND d.carSeater = :carSr")
    List<Driver> findAllByIsAvailableAndCarSeater(@Param("carSr") String carSr);

}