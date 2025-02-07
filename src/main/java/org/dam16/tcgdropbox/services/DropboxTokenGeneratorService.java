package org.dam16.tcgdropbox.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;

@Service
public class DropboxTokenGeneratorService {
    @Value("${dropbox.client.id}")
    private String clientId;

    @Value("${dropbox.client.secret}")
    private String clientSecret;

    @Value("${dropbox.refresh.token}")
    private String refreshToken;

    private static final String TOKEN_URL = "https://api.dropboxapi.com/oauth2/token";

    public String getAccessToken() {
        // Autenticación básica: encode clientId:clientSecret
        String credentials = clientId + ":" + clientSecret;
        String basicAuth = Base64.getEncoder().encodeToString(credentials.getBytes());

        // Crear las cabeceras
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + basicAuth);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Crear el cuerpo de la solicitud (usando el refresh_token)
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);

        // Crear la solicitud HTTP
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        // Realizar la solicitud POST para obtener el access token
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(TOKEN_URL, HttpMethod.POST, request, String.class);

        // Procesar la respuesta
        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                // Usamos Jackson para parsear el JSON de la respuesta
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                String accessToken = rootNode.path("access_token").asText();

                if (!accessToken.isEmpty()) {
                    System.out.println("Access Token: " + accessToken);
                    return accessToken;
                } else {
                    System.out.println("Error: No se encontró el access token en la respuesta.");
                    return null;
                }
            } catch (Exception e) {
                System.out.println("Error al procesar la respuesta JSON: " + e.getMessage());
                e.printStackTrace();
                return null;
            }
        } else {
            System.out.println("Error al obtener el access token. Código de estado: " + response.getStatusCode());
            return null;
        }
    }
}