package app.repository;

import app.entity.Booking;
import app.entity.Payment;
import app.entity.PaymentStatus;
import com.travelmate.model.User;
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
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByBooking(Booking booking);

    Optional<Payment> findByTransactionReference(String transactionReference);

    boolean existsByTransactionReference(String transactionReference);

    List<Payment> findByBooking_Vendor(User vendor);

    Page<Payment> findByBooking_Vendor(User vendor, Pageable pageable);

    List<Payment> findByBooking_Customer(User customer);

    Page<Payment> findByBooking_Customer(User customer, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.booking.id = :bookingId AND p.paymentStatus = 'COMPLETED'")
    BigDecimal sumPaidAmountForBooking(@Param("bookingId") Long bookingId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.booking.vendor = :vendor " +
           "AND p.paymentStatus = 'COMPLETED' AND p.paymentDate BETWEEN :start AND :end")
    BigDecimal sumCollectedPaymentsForVendor(
            @Param("vendor") User vendor,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE " +
           "p.paymentStatus = 'COMPLETED' AND p.paymentDate BETWEEN :start AND :end")
    BigDecimal sumCollectedPaymentsSystem(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
