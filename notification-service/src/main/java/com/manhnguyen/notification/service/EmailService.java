package com.manhnguyen.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    public void sendBookingConfirmation(String email, Long bookingId) {
        log.info("[EMAIL] Booking confirmation sent to {} for booking #{}", email, bookingId);
    }

    public void sendBookingCancellation(String email, Long bookingId) {
        log.info("[EMAIL] Booking cancellation sent to {} for booking #{}", email, bookingId);
    }

    public void sendPaymentConfirmation(String email, Long bookingId, String amount) {
        log.info("[EMAIL] Payment confirmation sent to {} for booking #{} amount {}", email, bookingId, amount);
    }

    public void sendRefundNotification(String email, Long bookingId, String amount) {
        log.info("[EMAIL] Refund notification sent to {} for booking #{} amount {}", email, bookingId, amount);
    }
}
