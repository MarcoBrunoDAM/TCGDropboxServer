package org.dam16.tcgdropbox.controllers;

import com.dropbox.core.InvalidAccessTokenException;
import org.dam16.tcgdropbox.services.DropboxService;
import org.dam16.tcgdropbox.services.DropboxTokenGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/dropbox")
public class ImagesController {

    @Autowired
    private DropboxService dropboxService;
    @Autowired
    private DropboxTokenGeneratorService dropboxTokenGeneratorService;
    String token="sl.CFXPiH2shNg4SyAVQygoic9T1fTkeU4OURZrcBWPtw3HUzRve8g5N5a5_cUq8Kwq_UP-BArV_K8nAduA2tTUp2OhFgLuhq2g-bNQ_h-iYVjpguzvsxWVMva1rg5ls-0jAb1U_kQIa9waRrr4NbuUf3w";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file")MultipartFile file){
        File tempFile = null;
        String dropboxUrl = "";
        try{
            String fileName = file.getOriginalFilename();
            tempFile = File.createTempFile(00+fileName, ".temp");
            file.transferTo(tempFile);

            dropboxService.uploadFileToDropbox(tempFile.getAbsolutePath(),"/images/"+fileName, token );
            //dropboxUrl = dropboxService.getDropboxImageUrl("/images/"+fileName, token);
            return ResponseEntity.ok("Imagen subida con éxito. URL: " + dropboxUrl);

        }catch (InvalidAccessTokenException e) {
            token = dropboxTokenGeneratorService.getAccessToken();
            if(token == null){
                return new ResponseEntity<>("El token no es valido o no se pudo renovar " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
            try{
                dropboxService.uploadFileToDropbox(tempFile.getAbsolutePath(),"/images/"+file.getOriginalFilename(), token );
                //dropboxUrl = dropboxService.getDropboxImageUrl("/images/"+file.getOriginalFilename(), token);
               // dropboxUrl = "http://localhost:8080/public/download/" + fileName;
                return ResponseEntity.ok("Imagen subida con éxito. URL: " + dropboxUrl);
            }catch (Exception ex){
                return new ResponseEntity<>("ERROR AL SUBIR LA IMAGEN: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }catch (Exception e){
            return new ResponseEntity<>("ERROR AL SUBIR LA IMAGEN: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }finally {
            if(tempFile != null && tempFile.exists()){
                tempFile.delete();
            }
        }
    }
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadImage(@PathVariable String fileName) {
        String dropboxFilePath = "/images/" + fileName; // Ruta del archivo en Dropbox
        try {
            byte[] imageBytes = dropboxService.downloadFileFromDropbox(dropboxFilePath, token);
            if (imageBytes == null) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageBytes);
        }catch (Exception e) {
            token = dropboxTokenGeneratorService.getAccessToken();
            if(token == null){
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
            try{
                byte[] imageBytes = dropboxService.downloadFileFromDropbox(dropboxFilePath, token);

                if (imageBytes == null) {
                    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(imageBytes);
            }catch (Exception ex){
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        }
    }
}
