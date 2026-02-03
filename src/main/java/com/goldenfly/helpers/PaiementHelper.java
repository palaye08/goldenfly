package com.goldenfly.helpers;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class PaiementHelper {

    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random random = new Random();

    /**
     * Génère un numéro de paiement unique
     * Format: PAY-YYYYMMDD-XXX
     */
    public String genererNumeroPaiement() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = generateRandomString(6);
        return "PAY-" + date + "-" + randomPart;
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }
}