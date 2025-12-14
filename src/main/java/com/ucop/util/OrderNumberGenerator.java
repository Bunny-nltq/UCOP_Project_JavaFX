package com.ucop.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class OrderNumberGenerator {
    private static final String PREFIX = "ORD";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Generate unique order number
     * Format: ORD-yyyyMMdd-XXXXX
     */
    public static String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(formatter);
        String randomPart = UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        return "%s-%s-%s".formatted(PREFIX, datePart, randomPart);
    }

    /**
     * Generate tracking number for shipment
     */
    public static String generateTrackingNumber() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(formatter);
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "TRK-%s-%s".formatted(datePart, randomPart);
    }

    /**
     * Generate appointment number
     */
    public static String generateAppointmentNumber() {
        LocalDateTime now = LocalDateTime.now();
        String datePart = now.format(formatter);
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "APT-%s-%s".formatted(datePart, randomPart);
    }
}
