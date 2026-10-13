package app.repository;

import app.entity.Maintenance;
import app.entity.MaintenanceStatus;
import com.travelmate.model.User;
import app.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findByVehicle(Vehicle vehicle);

    Page<Maintenance> findByVehicle(Vehicle vehicle, Pageable pageable);

    List<Maintenance> findByVehicle_Vendor(User vendor);

    Page<Maintenance> findByVehicle_Vendor(User vendor, Pageable pageable);

    List<Maintenance> findByStatus(MaintenanceStatus status);

    @Query("SELECT m FROM Maintenance m WHERE m.vehicle.vendor = :vendor AND m.nextServiceDate <= :threshold AND m.status != 'COMPLETED'")
    List<Maintenance> findUpcomingMaintenanceForVendor(@Param("vendor") User vendor, @Param("threshold") LocalDate threshold);

    @Query("SELECT COALESCE(SUM(m.serviceCost), 0) FROM Maintenance m WHERE m.vehicle = :vehicle")
    BigDecimal sumMaintenanceCostForVehicle(@Param("vehicle") Vehicle vehicle);

    @Query("SELECT COALESCE(SUM(m.serviceCost), 0) FROM Maintenance m WHERE m.vehicle.vendor = :vendor")
    BigDecimal sumMaintenanceCostForVendor(@Param("vendor") User vendor);

    @Query("SELECT m.vehicle.id, m.vehicle.name, COALESCE(SUM(m.serviceCost), 0) " +
           "FROM Maintenance m WHERE m.vehicle.vendor = :vendor " +
           "GROUP BY m.vehicle.id, m.vehicle.name")
    List<Object[]> findVehicleMaintenanceCostReportByVendor(@Param("vendor") User vendor);
}
