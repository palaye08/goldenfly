package com.goldenfly.design.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrangeMoneyService {

    @Value("${orange.api.url:https://api.orange.com/v1}")
    private String orangeApiUrl;

    @Value("${orange.api.key}")
    private String orangeApiKey;

    private final RestTemplate restTemplate;

    /**
     * Initier un paiement Orange Money
     * @return URL de paiement ou transaction ID
     */
    public String initierPaiement(String reference, Double montant, String numeroTelephone) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + orangeApiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", montant);
            requestBody.put("currency", "XOF");
            requestBody.put("order_id", reference);
            requestBody.put("customer_msisdn", numeroTelephone);
            requestBody.put("notif_url", "https://votre-domaine.com/api/paiements/orange/callback");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    orangeApiUrl + "/webpayment",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("payment_url");
            }

            throw new RuntimeException("Erreur lors de l'initiation du paiement Orange Money");

        } catch (Exception e) {
            log.error("Erreur Orange Money API: ", e);
            throw new RuntimeException("Erreur lors de la communication avec Orange Money: " + e.getMessage());
        }
    }
}