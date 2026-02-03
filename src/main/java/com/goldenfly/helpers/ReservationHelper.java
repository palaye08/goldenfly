package com.goldenfly.helpers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;

@Component
public class ReservationHelper {

    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random random = new Random();

    /**
     * Génère un numéro de réservation unique
     * Format: RES-YYYYMMDD-XXX
     */
    public String genererNumeroReservation() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = generateRandomString(6);
        return "RES-" + date + "-" + randomPart;
    }

    /**
     * Génère un QR code en base64 à partir d'une chaîne de données
     */
    public String genererQRCode(String data) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] qrCodeBytes = outputStream.toByteArray();

            return Base64.getEncoder().encodeToString(qrCodeBytes);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du QR code", e);
        }
    }

    /**
     * Calcule le prix total d'une réservation
     */
    public Double calculerPrixTotal(Double prixBase, double multiplicateurClasse, int nombrePassagers) {
        return prixBase * multiplicateurClasse * nombrePassagers;
    }

    /**
     * Calcule le prix pour un vol aller-retour
     */
    public Double calculerPrixAllerRetour(Double prixAller, Double prixRetour,
                                          double multiplicateurClasse, int nombrePassagers) {
        return (prixAller + prixRetour) * multiplicateurClasse * nombrePassagers;
    }

    /**
     * Vérifie si un vol est disponible pour une date donnée
     */
    public boolean isVolDisponiblePourDate(java.time.DayOfWeek jourSemaine,
                                           Boolean lundi, Boolean mardi, Boolean mercredi,
                                           Boolean jeudi, Boolean vendredi, Boolean samedi,
                                           Boolean dimanche) {
        return switch (jourSemaine) {
            case MONDAY -> lundi;
            case TUESDAY -> mardi;
            case WEDNESDAY -> mercredi;
            case THURSDAY -> jeudi;
            case FRIDAY -> vendredi;
            case SATURDAY -> samedi;
            case SUNDAY -> dimanche;
        };
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }
}