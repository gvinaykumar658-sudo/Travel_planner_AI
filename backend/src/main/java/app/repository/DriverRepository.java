package app.repository;

import app.entity.Driver;
import app.entity.DriverStatus;
import com.travelmate.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

    Optional<Driver> findByLicenceNumber(String licenceNumber);

    boolean existsByLicenceNumber(String licenceNumber);

    boolean existsByLicenceNumberAndIdNot(String licenceNumber, Long id);

    List<Driver> findByVendor(User vendor);

    Page<Driver> findByVendor(User vendor, Pageable pageable);

    List<Driver> findByVendorAndStatus(User vendor, DriverStatus status);

    long countByVendor(User vendor);

    @Query("SELECT d FROM Driver d WHERE d.vendor = :vendor AND d.licenceExpiryDate <= :threshold")
    List<Driver> findExpiringDriversForVendor(@Param("vendor") User vendor, @Param("threshold") LocalDate threshold);

    @Query("SELECT d FROM Driver d WHERE d.licenceExpiryDate <= :threshold")
    List<Driver> findAllExpiringDrivers(@Param("threshold") LocalDate threshold);
}
