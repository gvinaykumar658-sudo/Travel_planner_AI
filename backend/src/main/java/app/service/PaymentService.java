package app.service;

import com.travelmate.model.User;
import com.travelmate.model.Role;

import app.dto.PaymentRequest;
import app.dto.PaymentResponse;
import app.entity.*;
import app.exception.ConflictException;
import app.exception.ResourceNotFoundException;
import app.repository.BookingRepository;
import app.repository.NotificationRepository;
import app.repository.PaymentRepository;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public PaymentResponse recordPayment(PaymentRequest req, Long userId, Role role) {
        Booking booking = bookingRepository.findById(req.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + req.getBookingId()));

        if (role == Role.CUSTOMER && !booking.getCustomer().getId().equals(userId)) {
            throw new AccessDeniedException("You can only submit payments for your own bookings.");
        }
        if (role == Role.VENDOR && !booking.getVendor().getId().equals(userId)) {
            throw new AccessDeniedException("You can only record payments for bookings associated with your business.");
        }

        String txnRef = req.getTransactionReference().trim();
        if (paymentRepository.existsByTransactionReference(txnRef)) {
            throw new ConflictException("A payment transaction with reference '" + txnRef + "' already exists.");
        }

        Payment payment = Payment.builder()
                .booking(booking)
                .amount(req.getAmount())
                .paymentMethod(req.getPaymentMethod())
                .paymentStatus(PaymentStatus.COMPLETED)
                .transactionReference(txnRef)
                .paymentDate(LocalDateTime.now())
                .notes(req.getNotes())
                .build();

        Payment saved = paymentRepository.save(payment);

        // Update booking's overall payment status
        BigDecimal totalPaid = paymentRepository.sumPaidAmountForBooking(booking.getId());
        if (totalPaid.compareTo(booking.getTotalAmount()) >= 0) {
            booking.setPaymentStatus(PaymentStatus.COMPLETED);
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            booking.setPaymentStatus(PaymentStatus.PARTIAL);
        }
        bookingRepository.save(booking);

        // Send notifications
        notificationRepository.save(Notification.builder()
                .recipient(booking.getCustomer())
                .title("Payment Recorded: ₹" + req.getAmount())
                .message("Payment of ₹" + req.getAmount() + " for booking " +
                        booking.getBookingReference() + " has been received successfully.")
                .type(NotificationType.PAYMENT_RECEIVED)
                .referenceId(booking.getId())
                .build());

        return mapToResponse(saved, booking, totalPaid);
    }

    public Page<PaymentResponse> getPayments(Long userId, Role role, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (role == Role.ADMIN) {
            return paymentRepository.findAll(pageable)
                    .map(p -> mapToResponse(p, p.getBooking(), paymentRepository.sumPaidAmountForBooking(p.getBooking().getId())));
        } else if (role == Role.VENDOR) {
            return paymentRepository.findByBooking_Vendor(user, pageable)
                    .map(p -> mapToResponse(p, p.getBooking(), paymentRepository.sumPaidAmountForBooking(p.getBooking().getId())));
        } else {
            return paymentRepository.findByBooking_Customer(user, pageable)
                    .map(p -> mapToResponse(p, p.getBooking(), paymentRepository.sumPaidAmountForBooking(p.getBooking().getId())));
        }
    }

    public PaymentResponse getPaymentById(Long id, Long userId, Role role) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));

        Booking booking = payment.getBooking();
        if (role == Role.CUSTOMER && !booking.getCustomer().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have access to this receipt.");
        }
        if (role == Role.VENDOR && !booking.getVendor().getId().equals(userId)) {
            throw new AccessDeniedException("You do not have access to this payment record.");
        }

        BigDecimal totalPaid = paymentRepository.sumPaidAmountForBooking(booking.getId());
        return mapToResponse(payment, booking, totalPaid);
    }

    private PaymentResponse mapToResponse(Payment p, Booking b, BigDecimal totalPaid) {
        BigDecimal balance = b.getTotalAmount().subtract(totalPaid);
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            balance = BigDecimal.ZERO;
        }

        return PaymentResponse.builder()
                .id(p.getId())
                .bookingId(b.getId())
                .bookingReference(b.getBookingReference())
                .customerId(b.getCustomer().getId())
                .customerName(b.getCustomer().getName())
                .customerEmail(b.getCustomer().getEmail())
                .vendorId(b.getVendor().getId())
                .vendorBusinessName(b.getVendor().getBusinessName())
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .paymentStatus(p.getPaymentStatus())
                .transactionReference(p.getTransactionReference())
                .paymentDate(p.getPaymentDate())
                .notes(p.getNotes())
                .bookingTotalAmount(b.getTotalAmount())
                .totalPaidToDate(totalPaid)
                .outstandingBalance(balance)
                .createdAt(p.getCreatedAt())
                .build();
    }
}
