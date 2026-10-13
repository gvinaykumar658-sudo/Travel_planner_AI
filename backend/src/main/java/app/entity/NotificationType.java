package app.entity;

import com.travelmate.model.User;
import com.travelmate.model.Role;

public enum NotificationType {
    BOOKING_REQUEST,
    BOOKING_CONFIRMED,
    BOOKING_CANCELLED,
    UPCOMING_TRIP,
    PAYMENT_RECEIVED,
    PAYMENT_STATUS_CHANGE,
    INSURANCE_EXPIRY,
    RC_EXPIRY,
    PERMIT_EXPIRY,
    LICENCE_EXPIRY,
    MAINTENANCE_DUE,
    SYSTEM
}
