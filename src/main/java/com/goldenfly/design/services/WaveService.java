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
public class WaveService {

    @Value("${wave.api.url:https://api.wave.com/v1}")
    private String waveApiUrl;

    @Value("${wave.api.key}")
    private String waveApiKey;

    private final RestTemplate restTemplate;

    /**
     * Initier un paiement Wave
     * @return URL de checkout Wave
     */
    public String initierPaiement(String reference, Double montant, String numeroTelephone) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(waveApiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", montant);
            requestBody.put("currency", "XOF");
            requestBody.put("reference", reference);
            requestBody.put("phone", numeroTelephone);
            requestBody.put("callback_url", "https://votre-domaine.com/api/paiements/wave/callback");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    waveApiUrl + "/checkout/create",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return (String) response.getBody().get("checkout_url");
            }

            throw new RuntimeException("Erreur lors de l'initiation du paiement Wave");

        } catch (Exception e) {
            log.error("Erreur Wave API: ", e);
            throw new RuntimeException("Erreur lors de la communication avec Wave: " + e.getMessage());
        }
    }
}