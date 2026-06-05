package com.MT_MX.demo.service;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SwiftFileApiScheduler {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiUrl = "http://localhost:8080/mt-to-mx";
    private final String apiUrlmx = "http://localhost:8080/mx-to-mt";

    private final Path incomingDir_mt = Paths.get("E:\\aa\\mt");
    private final Path processedDir = Paths.get("E:\\aa\\mt\\processed");
    private final Path errorDir = Paths.get("E:\\aa\\mt\\error");
    private final Path incomingDir_mx = Paths.get("E:\\aa\\mt\\mx_start");
    @Scheduled(fixedDelay = 600)
    public void processFiles() {
        File[] files = incomingDir_mt.toFile().listFiles((dir, name) -> name.endsWith(".txt"));

        if (files != null) {
            for (File file : files) {
                try {
                    // Wrap file as resource
                    FileSystemResource resource = new FileSystemResource(file);

                    // Prepare multipart request
                    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                    body.add("file", resource);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                    // POST to /mt-to-mx
                    ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);
                    System.out.println("API response for " + file.getName() + ": " + response.getBody());

                    // Move successfully processed file to processedDir
                   // Files.createDirectories(processedDir);
                    Path targetPath = processedDir.resolve(file.getName());
                    Files.move(file.toPath(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);


                } catch (Exception e) {
                    System.err.println("Failed to process file " + file.getName() + ": " + e.getMessage());
                    e.printStackTrace();

                    try {
                        Files.createDirectories(errorDir);
                        Path targetPath = errorDir.resolve(file.getName());
                        Files.move(file.toPath(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    } catch (Exception ex) {
                        System.err.println("Failed to move errored file " + file.getName() + ": " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    @Scheduled(fixedDelay = 600)
    public void processmxFiles() {
        File[] files = incomingDir_mx.toFile().listFiles((dir, name) -> name.endsWith(".txt"));

        if (files != null) {
            for (File file : files) {
                try {
                    // Wrap file as resource
                    FileSystemResource resource = new FileSystemResource(file);

                    // Prepare multipart request
                    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                    body.add("file", resource);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                    // POST to /mt-to-mx
                    ResponseEntity<String> response = restTemplate.postForEntity(apiUrlmx, requestEntity, String.class);
                    System.out.println("API response for " + file.getName() + ": " + response.getBody());

                    // Files.createDirectories(processedDir);
                    Path targetPath = processedDir.resolve(file.getName());
                    Files.move(file.toPath(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);


                } catch (Exception e) {
                    System.err.println("Failed to process file " + file.getName() + ": " + e.getMessage());
                    e.printStackTrace();

                    // Move failed file to an error directory
//                    try {
//                        Files.createDirectories(errorDir);
//                        Path targetPath = errorDir.resolve(file.getName());
//                        Files.move(file.toPath(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
//                    } catch (Exception ex) {
//                        System.err.println("Failed to move errored file " + file.getName() + ": " + ex.getMessage());
//                        ex.printStackTrace();
//                    }
                }
            }
        }
    }
}
