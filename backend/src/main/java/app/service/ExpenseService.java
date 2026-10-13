package app.service;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.dto.ExpenseRequest;
import app.dto.ExpenseResponse;
import app.entity.*;
import app.exception.ResourceNotFoundException;
import app.repository.ExpenseRepository;
import app.repository.UserRepository;
import app.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public ExpenseResponse createExpense(ExpenseRequest req, Long vendorId) {
        User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor account not found."));

        Vehicle vehicle = null;
        if (req.getVehicleId() != null) {
            vehicle = vehicleRepository.findById(req.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + req.getVehicleId()));
            if (!vehicle.getVendor().getId().equals(vendorId)) {
                throw new AccessDeniedException("You can only log expenses for your own vehicles.");
            }
        }

        Expense expense = Expense.builder()
                .vendor(vendor)
                .vehicle(vehicle)
                .category(req.getCategory())
                .description(req.getDescription().trim())
                .amount(req.getAmount())
                .expenseDate(req.getExpenseDate())
                .receiptReference(req.getReceiptReference())
                .build();

        return mapToResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse updateExpense(Long id, ExpenseRequest req, Long vendorId, boolean isAdmin) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (!isAdmin && !expense.getVendor().getId().equals(vendorId)) {
            throw new AccessDeniedException("You do not have permission to modify this expense.");
        }

        Vehicle vehicle = null;
        if (req.getVehicleId() != null) {
            vehicle = vehicleRepository.findById(req.getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found."));
            if (!isAdmin && !vehicle.getVendor().getId().equals(vendorId)) {
                throw new AccessDeniedException("Vehicle does not belong to your business.");
            }
        }

        expense.setVehicle(vehicle);
        expense.setCategory(req.getCategory());
        expense.setDescription(req.getDescription().trim());
        expense.setAmount(req.getAmount());
        expense.setExpenseDate(req.getExpenseDate());
        expense.setReceiptReference(req.getReceiptReference());

        return mapToResponse(expenseRepository.save(expense));
    }

    public Page<ExpenseResponse> getExpenses(
            Long vendorId,
            ExpenseCategory category,
            Long vehicleId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found."));

        return expenseRepository.filterExpenses(vendor, category, vehicleId, startDate, endDate, pageable)
                .map(this::mapToResponse);
    }

    public ExpenseResponse getExpenseById(Long id, Long vendorId, boolean isAdmin) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (!isAdmin && !expense.getVendor().getId().equals(vendorId)) {
            throw new AccessDeniedException("You do not have access to this expense.");
        }

        return mapToResponse(expense);
    }

    @Transactional
    public void deleteExpense(Long id, Long vendorId, boolean isAdmin) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));

        if (!isAdmin && !expense.getVendor().getId().equals(vendorId)) {
            throw new AccessDeniedException("You do not have permission to delete this expense.");
        }

        expenseRepository.delete(expense);
    }

    private ExpenseResponse mapToResponse(Expense e) {
        return ExpenseResponse.builder()
                .id(e.getId())
                .vendorId(e.getVendor().getId())
                .vendorBusinessName(e.getVendor().getBusinessName())
                .vehicleId(e.getVehicle() != null ? e.getVehicle().getId() : null)
                .vehicleName(e.getVehicle() != null ? e.getVehicle().getName() : null)
                .vehicleRegistrationNumber(e.getVehicle() != null ? e.getVehicle().getRegistrationNumber() : null)
                .category(e.getCategory())
                .description(e.getDescription())
                .amount(e.getAmount())
                .expenseDate(e.getExpenseDate())
                .receiptReference(e.getReceiptReference())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
