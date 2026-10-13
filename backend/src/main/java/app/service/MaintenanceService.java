package app.service;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.dto.MaintenanceRequest;
import app.dto.MaintenanceResponse;
import app.entity.*;
import app.exception.ResourceNotFoundException;
import app.repository.MaintenanceRepository;
import app.repository.UserRepository;
import app.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Transactional
    public MaintenanceResponse scheduleMaintenance(MaintenanceRequest req, Long vendorId) {
        Vehicle vehicle = vehicleRepository.findById(req.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + req.getVehicleId()));

        if (!vehicle.getVendor().getId().equals(vendorId)) {
            throw new AccessDeniedException("You can only schedule maintenance for your own vehicles.");
        }

        // If maintenance is in progress, flag the vehicle so it cannot be rented
        if (req.getStatus() == MaintenanceStatus.IN_PROGRESS) {
            vehicle.setStatus(VehicleStatus.MAINTENANCE);
            vehicleRepository.save(vehicle);
        }

        Maintenance maintenance = Maintenance.builder()
                .vehicle(vehicle)
                .serviceType(req.getServiceType().trim())
                .description(req.getDescription())
                .serviceDate(req.getServiceDate())
                .kilometerReading(req.getKilometerReading())
                .serviceCost(req.getServiceCost())
                .nextServiceDate(req.getNextServiceDate())
                .nextServiceKilometerReading(req.getNextServiceKilometerReading())
                .status(req.getStatus() != null ? req.getStatus() : MaintenanceStatus.SCHEDULED)
                .build();

        return mapToResponse(maintenanceRepository.save(maintenance));
    }

    @Transactional
    public MaintenanceResponse updateMaintenance(Long id, MaintenanceRequest req, Long vendorId, boolean isAdmin) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance record not found with id: " + id));

        Vehicle vehicle = maintenance.getVehicle();
        if (!isAdmin && !vehicle.getVendor().getId().equals(vendorId)) {
            throw new AccessDeniedException("You do not have permission to modify this maintenance record.");
        }

        maintenance.setServiceType(req.getServiceType().trim());
        maintenance.setDescription(req.getDescription());
        maintenance.setServiceDate(req.getServiceDate());
        maintenance.setKilometerReading(req.getKilometerReading());
        maintenance.setServiceCost(req.getServiceCost());
        maintenance.setNextServiceDate(req.getNextServiceDate());
        maintenance.setNextServiceKilometerReading(req.getNextServiceKilometerReading());

        MaintenanceStatus oldStatus = maintenance.getStatus();
        maintenance.setStatus(req.getStatus());

        // Status transition effects
        if (req.getStatus() == MaintenanceStatus.IN_PROGRESS) {
            vehicle.setStatus(VehicleStatus.MAINTENANCE);
            vehicleRepository.save(vehicle);
        } else if (req.getStatus() == MaintenanceStatus.COMPLETED) {
            // Update vehicle current km if service was higher
            if (req.getKilometerReading() > vehicle.getCurrentKm()) {
                vehicle.setCurrentKm(req.getKilometerReading());
            }
            // Restore vehicle to AVAILABLE if it was marked MAINTENANCE
            if (vehicle.getStatus() == VehicleStatus.MAINTENANCE) {
                vehicle.setStatus(VehicleStatus.AVAILABLE);
            }
            vehicleRepository.save(vehicle);
        }

        return mapToResponse(maintenanceRepository.save(maintenance));
    }

    public Page<MaintenanceResponse> getMaintenanceByVendor(Long vendorId, Pageable pageable) {
        User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found."));

        return maintenanceRepository.findByVehicle_Vendor(vendor, pageable).map(this::mapToResponse);
    }

    public List<MaintenanceResponse> getUpcomingMaintenance(Long vendorId) {
        User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found."));
        LocalDate threshold = LocalDate.now().plusDays(30);

        return maintenanceRepository.findUpcomingMaintenanceForVendor(vendor, threshold)
                .stream().map(this::mapToResponse).toList();
    }

    private MaintenanceResponse mapToResponse(Maintenance m) {
        LocalDate now = LocalDate.now();
        boolean overdue = false;
        boolean dueSoon = false;
        String alert = null;

        if (m.getNextServiceDate() != null) {
            if (m.getNextServiceDate().isBefore(now)) {
                overdue = true;
                alert = "Service overdue since " + m.getNextServiceDate();
            } else if (m.getNextServiceDate().isBefore(now.plusDays(15))) {
                dueSoon = true;
                alert = "Service due soon on " + m.getNextServiceDate();
            }
        }

        return MaintenanceResponse.builder()
                .id(m.getId())
                .vehicleId(m.getVehicle().getId())
                .vehicleName(m.getVehicle().getName())
                .vehicleRegistrationNumber(m.getVehicle().getRegistrationNumber())
                .serviceType(m.getServiceType())
                .description(m.getDescription())
                .serviceDate(m.getServiceDate())
                .kilometerReading(m.getKilometerReading())
                .serviceCost(m.getServiceCost())
                .nextServiceDate(m.getNextServiceDate())
                .nextServiceKilometerReading(m.getNextServiceKilometerReading())
                .status(m.getStatus())
                .isOverdue(overdue)
                .isDueSoon(dueSoon)
                .alert(alert)
                .createdAt(m.getCreatedAt())
                .build();
    }
}
