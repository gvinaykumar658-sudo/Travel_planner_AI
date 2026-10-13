package app.repository;

import com.travelmate.model.User;
import app.entity.Vehicle;
import app.entity.VehicleStatus;
import app.entity.VehicleType;
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
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    boolean existsByRegistrationNumberAndIdNot(String registrationNumber, Long id);

    List<Vehicle> findByVendor(User vendor);

    Page<Vehicle> findByVendor(User vendor, Pageable pageable);

    List<Vehicle> findByStatus(VehicleStatus status);

    Page<Vehicle> findByStatus(VehicleStatus status, Pageable pageable);

    List<Vehicle> findByVendorAndStatus(User vendor, VehicleStatus status);

    long countByVendor(User vendor);

    long countByStatus(VehicleStatus status);

    @Query("SELECT v FROM Vehicle v WHERE " +
           "(:status IS NULL OR v.status = :status OR (:status = app.entity.VehicleStatus.AVAILABLE AND v.status = app.entity.VehicleStatus.APPROVED)) AND " +
           "(:type IS NULL OR v.vehicleType = :type) AND " +
           "(:capacity IS NULL OR v.seatingCapacity >= :capacity) AND " +
           "(:search IS NULL OR LOWER(v.name) LIKE LOWER(CONCAT('%', :search, '%')) " +
           " OR LOWER(v.brand) LIKE LOWER(CONCAT('%', :search, '%')) " +
           " OR LOWER(v.model) LIKE LOWER(CONCAT('%', :search, '%')) " +
           " OR LOWER(v.registrationNumber) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Vehicle> searchVehicles(
            @Param("status") VehicleStatus status,
            @Param("type") VehicleType type,
            @Param("capacity") Integer capacity,
            @Param("search") String search,
            Pageable pageable);

    // Document expiry checks
    @Query("SELECT v FROM Vehicle v WHERE v.vendor = :vendor AND " +
           "(v.insuranceExpiryDate <= :threshold OR v.rcExpiryDate <= :threshold OR v.permitExpiryDate <= :threshold)")
    List<Vehicle> findExpiringVehiclesForVendor(@Param("vendor") User vendor, @Param("threshold") LocalDate threshold);

    @Query("SELECT v FROM Vehicle v WHERE " +
           "(v.insuranceExpiryDate <= :threshold OR v.rcExpiryDate <= :threshold OR v.permitExpiryDate <= :threshold)")
    List<Vehicle> findAllExpiringVehicles(@Param("threshold") LocalDate threshold);
}
