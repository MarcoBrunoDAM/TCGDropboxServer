package org.dam16.tcgdropbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DropboxOAuth {
    public static String getAccessToken(String code) throws IOException {
        // URL del endpoint para obtener el token
        String tokenUrl = "https://api.dropboxapi.com/oauth2/token";

        // Datos del POST: Código, client_id, client_secret, y redirect_uri
        String params = "code=" + code +
                "&grant_type=authorization_code" +
                "&client_id=" +
                "46gz68sxd2o1naa" +
                "&client_secret=bgsaqjyjht9wq5v" +
                "&redirect_uri=http://localhost";

        // Abrimos la conexión
        URL url = new URL(tokenUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        // Enviamos los parámetros del POST
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = params.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Recibimos la respuesta
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        // Aquí la respuesta será el token de acceso en formato JSON
        return response.toString();
    }

    public static void main(String[] args) throws IOException {
        // El código que recibes en el callback
        String code = "6-YbO0KDUHUAAAAAAAAAIGJkPl_bhFhGB9cDJGa1Ozs";
        String response = getAccessToken(code);
        System.out.println("Respuesta: " + response);

    }
}
