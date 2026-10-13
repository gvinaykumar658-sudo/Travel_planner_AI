package app.repository;

import app.entity.Expense;
import app.entity.ExpenseCategory;
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
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByVendor(User vendor);

    Page<Expense> findByVendor(User vendor, Pageable pageable);

    List<Expense> findByVehicle(Vehicle vehicle);

    List<Expense> findByVendorAndCategory(User vendor, ExpenseCategory category);

    @Query("SELECT e FROM Expense e WHERE e.vendor = :vendor " +
           "AND (:categoryId IS NULL OR e.category = :categoryId) " +
           "AND (:vehicleId IS NULL OR e.vehicle.id = :vehicleId) " +
           "AND (:startDate IS NULL OR e.expenseDate >= :startDate) " +
           "AND (:endDate IS NULL OR e.expenseDate <= :endDate)")
    Page<Expense> filterExpenses(
            @Param("vendor") User vendor,
            @Param("categoryId") ExpenseCategory categoryId,
            @Param("vehicleId") Long vehicleId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.vendor = :vendor " +
           "AND e.expenseDate BETWEEN :start AND :end")
    BigDecimal sumExpensesForVendor(
            @Param("vendor") User vendor,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.expenseDate BETWEEN :start AND :end")
    BigDecimal sumExpensesSystem(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT e.category, COALESCE(SUM(e.amount), 0) FROM Expense e WHERE e.vendor = :vendor GROUP BY e.category")
    List<Object[]> sumExpensesGroupedByCategory(@Param("vendor") User vendor);
}
