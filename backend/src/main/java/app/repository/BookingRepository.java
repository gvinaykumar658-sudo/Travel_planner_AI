package app.repository;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(String bookingReference);

    List<Booking> findByCustomer(User customer);

    Page<Booking> findByCustomer(User customer, Pageable pageable);

    List<Booking> findByVendor(User vendor);

    Page<Booking> findByVendor(User vendor, Pageable pageable);

    List<Booking> findByVehicle(Vehicle vehicle);

    List<Booking> findByVehicleIn(List<Vehicle> vehicles);

    List<Booking> findByDriver(Driver driver);

    // Vehicle overlap check: checks if any active booking overlaps with the requested interval
    @Query("SELECT b FROM Booking b WHERE b.vehicle.id = :vehicleId " +
           "AND b.bookingStatus NOT IN ('CANCELLED') " +
           "AND (:excludeBookingId IS NULL OR b.id != :excludeBookingId) " +
           "AND b.pickupDateTime < :returnTime AND b.returnDateTime > :pickupTime")
    List<Booking> findConflictingVehicleBookings(
            @Param("vehicleId") Long vehicleId,
            @Param("pickupTime") LocalDateTime pickupTime,
            @Param("returnTime") LocalDateTime returnTime,
            @Param("excludeBookingId") Long excludeBookingId);

    // Driver overlap check: checks if a driver is assigned to another active trip during the same interval
    @Query("SELECT b FROM Booking b WHERE b.driver.id = :driverId " +
           "AND b.bookingStatus NOT IN ('CANCELLED', 'COMPLETED') " +
           "AND (:excludeBookingId IS NULL OR b.id != :excludeBookingId) " +
           "AND b.pickupDateTime < :returnTime AND b.returnDateTime > :pickupTime")
    List<Booking> findConflictingDriverBookings(
            @Param("driverId") Long driverId,
            @Param("pickupTime") LocalDateTime pickupTime,
            @Param("returnTime") LocalDateTime returnTime,
            @Param("excludeBookingId") Long excludeBookingId);

    // Reporting aggregations
    long countByVendorAndBookingStatus(User vendor, BookingStatus status);

    long countByBookingStatus(BookingStatus status);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.vendor = :vendor " +
           "AND b.bookingStatus NOT IN ('CANCELLED') " +
           "AND b.pickupDateTime BETWEEN :start AND :end")
    BigDecimal sumTotalRevenueForVendor(
            @Param("vendor") User vendor,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE " +
           "b.bookingStatus NOT IN ('CANCELLED') " +
           "AND b.pickupDateTime BETWEEN :start AND :end")
    BigDecimal sumTotalRevenueSystem(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT b.vehicle.id, b.vehicle.name, COUNT(b), COALESCE(SUM(b.totalAmount), 0) " +
           "FROM Booking b WHERE b.vendor = :vendor AND b.bookingStatus NOT IN ('CANCELLED') " +
           "GROUP BY b.vehicle.id, b.vehicle.name")
    List<Object[]> findVehicleIncomeReportByVendor(@Param("vendor") User vendor);

    @Query("SELECT DISTINCT b.customer FROM Booking b WHERE b.vendor = :vendor")
    List<User> findDistinctCustomersByVendor(@Param("vendor") User vendor);

    @Query("SELECT DISTINCT b.customer FROM Booking b WHERE b.vendor = :vendor")
    Page<User> findDistinctCustomersByVendor(@Param("vendor") User vendor, Pageable pageable);
}
