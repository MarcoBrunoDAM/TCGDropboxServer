package org.dam16.tcgdropbox.controllers;

import com.dropbox.core.InvalidAccessTokenException;
import org.dam16.tcgdropbox.services.DropboxService;
import org.dam16.tcgdropbox.services.DropboxTokenGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    String token="sl.u.AFjxgNcFQtbGGuttIYnDwy7S85lLjcBXFWojpE_sWwZGrmWK8H1W__gVcV0z1C1JK1oY90AH4IbVTsn1xxPCo8b-nytbdfmytSRmMjD9MxrBqXSNO_3M2UYXlZhB0TgB77yGvaKBmvgi8qkGuyygg1PTM8D7vTFtxmr1JRzEpFe6nn4_-0UfX7cC6vOtT0ZU3TwLZjy5xXZpJ3pQrIJ3md1mnxsr_AK7PhJCoiP0utGSY4KnfyeoFr_L3adSPY8gHQA3i4W_YFQaG33kqb5mSgohIy2gRt8AthO1U2_D8loXVlK45hkzbWptoIpTD7IH0o7ybmJhXfzvBztvq_8BFoIjng_pqmn0xeo3EtmvWGekcjAcFVcJm8FuBUcCbo6c0smY6zPhFEvXiMP0zLesg6yNpTIvTZz_ZGWMlL8eNgzkP-A-iehWTlKY9BQFPBFLSGJUqZ7Jqrv-IBSivO-4nQM_gliqkVE_ta_wIGeEv6qeQftmtuBzinOkk9TkvJF7nIC1mOV5v4AKVObvlCh6jY9cKnrg947As704xZCfX1QajtkmI1e686Wpwrx_LROwjUIY4SF2FOzIFrtpKMwvzYB-6JFL_UgO4cuWQTHa0ibfVvbyW8SZH92sbi-04DchEkpcrkCyplV7WlWtF1wfv_aietRXhHBD8Vk1Rmc3zkHxyWdLurt27OU0bSuxCaMYPQv2AGTRJ7aNWn-XjKxv_YEzFZpLJMu6ePv1cBYpCVvpBKVtY91aG_14DEactJimAujo_B6ZG04oWMP5V8a_-lqk4ZllvwhO4wHnJiR7iN-2YxFJXHouROmrrg6v4N7PYu9ey9sH6xuwCVxAio7ds_YyTPZj42ebdMoKzsOBOXNRaIwbxw0MPnGSvaCQGVoY1g8gAk_e6jzIkQeTnrIK8mloAoUgvMNbxPfJSS3fygrx7kP2uoa_y-N2k92SlOTQRv522hMUM9_Az7vLRd8xWfoDsdrx_RNH_9vc7HBgdZhlntuzbl6bejd2e1FA2C8v8DLrodfDfe6kfrVlNZXCDmlYJvPuy8WfZTGQ4h9kcT_huGxJSQCQBIHztNJsfeL0FflhhMcfuiKUacx93LM2fGizoSZ0IBqmLAztaXiV5W-FSZdPTJlWPV6Mb-f8WxS9BJUygBK_N91qIq9-QyvxCuHsD7OI8QSVB_gfusI9ddVFdjLX-p_Q35lLBaCtfnqw5iA4Yj8Aq3LvjuK3HabaBqwTU5sAsh9c9YN4hxO521hW8DaLb-Z7PjG1_9-wCB4dopT4MaWxFMAL5NLWDzs97Qjkl7FmPFDNL4Xzls8FWKM1CRTKkyp9fNMTiSxl7oYItoihd-GZpx96zbG4Y5JS2F79USSuF90Bf8-GN17o18S9oPflssz3shd4Jo6bPlAjMq4FuoqKZTsI1Ci3uAdml8LIG-p7b6G85CBtdZa6k6z_2Q";

    @PostMapping("/upload/card")
    public ResponseEntity<String> uploadImageCard(@RequestParam("file")MultipartFile file, @AuthenticationPrincipal UserDetails userDetails){
        if(userDetails != null){
            String admin = userDetails.getAuthorities().iterator().next().getAuthority();
            if(admin.equals("ROLE_true")) {
                File tempFile = null;
                String dropboxUrl = "";
                try {
                    String fileName = file.getOriginalFilename();
                    tempFile = File.createTempFile(00 + fileName, ".temp");
                    file.transferTo(tempFile);

                    dropboxService.uploadFileToDropbox(tempFile.getAbsolutePath(), "/cards/" + fileName, token);
                    //dropboxUrl = dropboxService.getDropboxImageUrl("/images/"+fileName, token);
                    return ResponseEntity.ok("Imagen subida con éxito. URL: " + dropboxUrl);

                } catch (InvalidAccessTokenException e) {
                    token = dropboxTokenGeneratorService.getAccessToken();
                    if (token == null) {
                        return new ResponseEntity<>("El token no es valido o no se pudo renovar " + e.getMessage(), HttpStatus.UNAUTHORIZED);
                    }
                    try {
                        dropboxService.uploadFileToDropbox(tempFile.getAbsolutePath(), "/cards/" + file.getOriginalFilename(), token);
                        //dropboxUrl = dropboxService.getDropboxImageUrl("/images/"+file.getOriginalFilename(), token);
                        // dropboxUrl = "http://localhost:8080/public/download/" + fileName;
                        return ResponseEntity.ok("Imagen subida con éxito. URL: " + dropboxUrl);
                    } catch (Exception ex) {
                        return new ResponseEntity<>("ERROR AL SUBIR LA IMAGEN: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } catch (Exception e) {
                    return new ResponseEntity<>("ERROR AL SUBIR LA IMAGEN: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } finally {
                    if (tempFile != null && tempFile.exists()) {
                        tempFile.delete();
                    }
                }
            }
        }
        return null;
    }
    @PostMapping("/upload/collection")
    public ResponseEntity<String> uploadImageCollection(@RequestParam("file")MultipartFile file,@AuthenticationPrincipal UserDetails userDetails){
        if(userDetails != null){
            String admin = userDetails.getAuthorities().iterator().next().getAuthority();
            if(admin.equals("ROLE_true")) {
                File tempFile = null;
                String dropboxUrl = "";
                try {
                    String fileName = file.getOriginalFilename();
                    tempFile = File.createTempFile(00 + fileName, ".temp");
                    file.transferTo(tempFile);

                    dropboxService.uploadFileToDropbox(tempFile.getAbsolutePath(), "/collections/" + fileName, token);
                    //dropboxUrl = dropboxService.getDropboxImageUrl("/images/"+fileName, token);
                    return ResponseEntity.ok("Imagen subida con éxito. URL: " + dropboxUrl);

                } catch (InvalidAccessTokenException e) {
                    token = dropboxTokenGeneratorService.getAccessToken();
                    if (token == null) {
                        return new ResponseEntity<>("El token no es valido o no se pudo renovar " + e.getMessage(), HttpStatus.UNAUTHORIZED);
                    }
                    try {
                        dropboxService.uploadFileToDropbox(tempFile.getAbsolutePath(), "/collections/" + file.getOriginalFilename(), token);
                        //dropboxUrl = dropboxService.getDropboxImageUrl("/images/"+file.getOriginalFilename(), token);
                        // dropboxUrl = "http://localhost:8080/public/download/" + fileName;
                        return ResponseEntity.ok("Imagen subida con éxito. URL: " + dropboxUrl);
                    } catch (Exception ex) {
                        return new ResponseEntity<>("ERROR AL SUBIR LA IMAGEN: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } catch (Exception e) {
                    return new ResponseEntity<>("ERROR AL SUBIR LA IMAGEN: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } finally {
                    if (tempFile != null && tempFile.exists()) {
                        tempFile.delete();
                    }
                }
            }
        }
        return null;
    }
    @PostMapping("/upload/product")
    public ResponseEntity<String> uploadImageProduct(@RequestParam("file")MultipartFile file,@AuthenticationPrincipal UserDetails userDetails){
        if(userDetails != null){
            String admin = userDetails.getAuthorities().iterator().next().getAuthority();
            if(admin.equals("ROLE_true")) {
                File tempFile = null;
                String dropboxUrl = "";
                try {
                    String fileName = file.getOriginalFilename();
                    tempFile = File.createTempFile(00 + fileName, ".temp");
                    file.transferTo(tempFile);

                    dropboxService.uploadFileToDropbox(tempFile.getAbsolutePath(), "/products/" + fileName, token);
                    //dropboxUrl = dropboxService.getDropboxImageUrl("/images/"+fileName, token);
                    return ResponseEntity.ok("Imagen subida con éxito. URL: " + dropboxUrl);

                } catch (InvalidAccessTokenException e) {
                    token = dropboxTokenGeneratorService.getAccessToken();
                    if (token == null) {
                        return new ResponseEntity<>("El token no es valido o no se pudo renovar " + e.getMessage(), HttpStatus.UNAUTHORIZED);
                    }
                    try {
                        dropboxService.uploadFileToDropbox(tempFile.getAbsolutePath(), "/products/" + file.getOriginalFilename(), token);
                        //dropboxUrl = dropboxService.getDropboxImageUrl("/images/"+file.getOriginalFilename(), token);
                        // dropboxUrl = "http://localhost:8080/public/download/" + fileName;
                        return ResponseEntity.ok("Imagen subida con éxito. URL: " + dropboxUrl);
                    } catch (Exception ex) {
                        return new ResponseEntity<>("ERROR AL SUBIR LA IMAGEN: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                } catch (Exception e) {
                    return new ResponseEntity<>("ERROR AL SUBIR LA IMAGEN: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                } finally {
                    if (tempFile != null && tempFile.exists()) {
                        tempFile.delete();
                    }
                }
            }
        }
        return null;
    }
    @GetMapping("/download/card/{fileName}")
    public ResponseEntity<byte[]> downloadImageCard(@PathVariable String fileName) {
        String dropboxFilePath = "/cards/" + fileName; // Ruta del archivo en Dropbox
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
    @GetMapping("/download/collection/{fileName}")
    public ResponseEntity<byte[]> downloadImageCollection(@PathVariable String fileName) {
        String dropboxFilePath = "/collections/" + fileName; // Ruta del archivo en Dropbox
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
    @GetMapping("/download/product/{fileName}")
    public ResponseEntity<byte[]> downloadImageProduct(@PathVariable String fileName) {
        String dropboxFilePath = "/products/" + fileName; // Ruta del archivo en Dropbox
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


    @GetMapping("/download/logo/{fileName}")
    public ResponseEntity<byte[]> downloadImageLogo(@PathVariable String fileName) {
        String dropboxFilePath = "/logo/" + fileName; // Ruta del archivo en Dropbox
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
