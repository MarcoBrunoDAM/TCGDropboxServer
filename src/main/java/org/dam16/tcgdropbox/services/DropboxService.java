package org.dam16.tcgdropbox.services;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.*;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class DropboxService {

    /**
     * Sube un archivo local a una ubicación específica en Dropbox.
     *
     * @param filePath               Ruta del archivo local que se quiere subir.
     * @param dropboxDestinationPath Ruta destino en Dropbox donde se almacenará el archivo.
     * @param token                  Token de acceso para autenticar con la API de Dropbox.
     * @throws Exception Si ocurre algún error durante el proceso de subida.
     */
    public void uploadFileToDropbox(String filePath, String dropboxDestinationPath, String token) throws Exception {
        // Configurar el cliente de Dropbox con un identificador de la aplicación.
        DbxRequestConfig config = DbxRequestConfig.newBuilder("TCGErcilla").build();

        // Crear una instancia de DbxClientV2 utilizando el token de acceso proporcionado.
        DbxClientV2 client = new DbxClientV2(config, token);

        // Crear un objeto File a partir de la ruta local proporcionada.
        File file = new File(filePath);

        // Utilizar un bloque try-with-resources para garantizar que el InputStream se cierre automáticamente.
        try (InputStream in = new FileInputStream(file)) {
            // Subir el archivo al destino especificado en Dropbox.
            FileMetadata metadata = client.files()
                    .uploadBuilder(dropboxDestinationPath) // Establecer la ruta destino en Dropbox.
                    .withMode(WriteMode.OVERWRITE)         // Configurar el modo de subida (sobrescribir si ya existe).
                    .uploadAndFinish(in);                 // Subir el archivo y cerrar automáticamente el InputStream.

            // Mostrar un mensaje de confirmación con la ruta del archivo subido.
            System.out.println("Archivo subido correctamente: " + metadata.getPathLower());
        } catch (UploadErrorException e) {
            // Capturar y manejar errores específicos de la subida del archivo.
            System.err.println("Error al subir el archivo a Dropbox: " + e.getMessage());
        }
    }
    public byte[] downloadFileFromDropbox(String dropboxFilePath, String token) throws IOException, DbxException {
        // Inicializar el cliente de Dropbox
        DbxRequestConfig config = DbxRequestConfig.newBuilder("TCGErcilla").build();
        DbxClientV2 client = new DbxClientV2(config, token);

        // Descargar el archivo desde Dropbox
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Descargar el archivo usando el builder y obtener el flujo de entrada
            client.files().downloadBuilder(dropboxFilePath)
                    .download(outputStream); // El flujo se escribe directamente en outputStream

            // Devolver los bytes del archivo descargado
            return outputStream.toByteArray();
        } catch (DbxException | IOException e) {
            // Manejar excepciones: puedes lanzar o registrar el error
            throw new IOException("Error al descargar el archivo desde Dropbox: " + e.getMessage(), e);
        }
    }
    public String getDropboxImageUrl(String dropboxFilePath, String token) throws IOException {
        DbxRequestConfig config = DbxRequestConfig.newBuilder("TCGErcilla").build();
        DbxClientV2 client = new DbxClientV2(config, token);

        // Obtener enlace temporal
        DbxUserFilesRequests files = client.files();
        GetTemporaryLinkResult result = null;
        try {
            result = files.getTemporaryLink(dropboxFilePath);
        } catch (DbxException e) {
            throw new RuntimeException(e);
        }

        return result.getLink(); // Devuelve el enlace temporal
    }
}