package app.service;

import app.dto.DriverRequest;
import app.dto.DriverResponse;
import app.entity.Driver;
import app.entity.DriverStatus;
import com.travelmate.model.User;
import app.exception.ConflictException;
import app.exception.ResourceNotFoundException;
import app.repository.DriverRepository;
import app.repository.UserRepository;
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
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    @Transactional
    public DriverResponse createDriver(DriverRequest req, Long vendorId) {
        String licence = req.getLicenceNumber().trim().toUpperCase();

        if (driverRepository.existsByLicenceNumber(licence)) {
            throw new ConflictException("Driver with licence number " + licence + " already exists.");
        }

        User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor account not found."));

        Driver driver = Driver.builder()
                .name(req.getName().trim())
                .phone(req.getPhone().trim())
                .email(req.getEmail() != null ? req.getEmail().trim().toLowerCase() : null)
                .licenceNumber(licence)
                .licenceExpiryDate(req.getLicenceExpiryDate())
                .address(req.getAddress() != null ? req.getAddress().trim() : null)
                .status(req.getStatus() != null ? req.getStatus() : DriverStatus.AVAILABLE)
                .vendor(vendor)
                .build();

        Driver saved = driverRepository.save(driver);
        return mapToResponse(saved);
    }

    @Transactional
    public DriverResponse updateDriver(Long id, DriverRequest req, Long vendorId, boolean isAdmin) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        if (!isAdmin && !driver.getVendor().getId().equals(vendorId)) {
            throw new AccessDeniedException("You can only modify drivers assigned to your business.");
        }

        String licence = req.getLicenceNumber().trim().toUpperCase();
        if (driverRepository.existsByLicenceNumberAndIdNot(licence, id)) {
            throw new ConflictException("Licence number " + licence + " is already used by another driver.");
        }

        driver.setName(req.getName().trim());
        driver.setPhone(req.getPhone().trim());
        driver.setEmail(req.getEmail() != null ? req.getEmail().trim().toLowerCase() : null);
        driver.setLicenceNumber(licence);
        driver.setLicenceExpiryDate(req.getLicenceExpiryDate());
        driver.setAddress(req.getAddress() != null ? req.getAddress().trim() : null);
        if (req.getStatus() != null) {
            driver.setStatus(req.getStatus());
        }

        Driver updated = driverRepository.save(driver);
        return mapToResponse(updated);
    }

    public DriverResponse getDriverById(Long id, Long vendorId, boolean isAdmin) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        if (!isAdmin && !driver.getVendor().getId().equals(vendorId)) {
            throw new AccessDeniedException("You do not have permission to view this driver.");
        }

        return mapToResponse(driver);
    }

    public Page<DriverResponse> getDriversByVendor(Long vendorId, DriverStatus status, Pageable pageable) {
        User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found."));

        if (status != null) {
            // Filter by vendor and status
            List<Driver> filtered = driverRepository.findByVendorAndStatus(vendor, status);
            return Page.empty(pageable); // fallback or use query
        }

        return driverRepository.findByVendor(vendor, pageable).map(this::mapToResponse);
    }

    public List<DriverResponse> getAvailableDrivers(Long vendorId) {
        User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found."));
        LocalDate now = LocalDate.now();

        return driverRepository.findByVendorAndStatus(vendor, DriverStatus.AVAILABLE).stream()
                .filter(d -> d.getLicenceExpiryDate() == null || !d.getLicenceExpiryDate().isBefore(now))
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void deleteDriver(Long id, Long vendorId, boolean isAdmin) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver not found with id: " + id));

        if (!isAdmin && !driver.getVendor().getId().equals(vendorId)) {
            throw new AccessDeniedException("You can only delete drivers from your business.");
        }

        driver.setStatus(DriverStatus.INACTIVE);
        driverRepository.save(driver);
    }

    public List<DriverResponse> getExpiringDrivers(Long vendorId, boolean isAdmin) {
        LocalDate threshold = LocalDate.now().plusDays(30);
        List<Driver> list;
        if (isAdmin) {
            list = driverRepository.findAllExpiringDrivers(threshold);
        } else {
            User vendor = userRepository.findById(vendorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found."));
            list = driverRepository.findExpiringDriversForVendor(vendor, threshold);
        }
        return list.stream().map(this::mapToResponse).toList();
    }

    private DriverResponse mapToResponse(Driver d) {
        LocalDate now = LocalDate.now();
        boolean expired = d.getLicenceExpiryDate().isBefore(now);
        boolean expiringSoon = !expired && d.getLicenceExpiryDate().isBefore(now.plusDays(30));

        String warning = null;
        if (expired) {
            warning = "Licence expired on " + d.getLicenceExpiryDate();
        } else if (expiringSoon) {
            warning = "Licence expiring soon on " + d.getLicenceExpiryDate();
        }

        return DriverResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .phone(d.getPhone())
                .email(d.getEmail())
                .licenceNumber(d.getLicenceNumber())
                .licenceExpiryDate(d.getLicenceExpiryDate())
                .address(d.getAddress())
                .status(d.getStatus())
                .vendorId(d.getVendor().getId())
                .vendorName(d.getVendor().getName())
                .vendorBusinessName(d.getVendor().getBusinessName())
                .isLicenceExpired(expired)
                .isLicenceExpiringSoon(expiringSoon)
                .warning(warning)
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
