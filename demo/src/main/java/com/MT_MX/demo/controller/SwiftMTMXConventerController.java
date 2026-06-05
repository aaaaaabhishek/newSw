package com.MT_MX.demo.controller;

import com.MT_MX.demo.service.SwiftMTMXConventerService;
import com.MT_MX.demo.service.SwiftMXMTConventerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
public class SwiftMTMXConventerController {

    private final SwiftMTMXConventerService conventerService;
    private final SwiftMXMTConventerService mtconventerService;

    public SwiftMTMXConventerController(SwiftMTMXConventerService conventerService, SwiftMXMTConventerService mtconventerService) {
        this.conventerService = conventerService;
        this.mtconventerService = mtconventerService;
    }
    @PostMapping("/mt-to-mx")
    public ResponseEntity<String> convertMTtoMX(
            @RequestParam("file") MultipartFile file
    ) {
       String mxMessage = conventerService.convert(file);
        return ResponseEntity.ok(mxMessage);
    }
    @PostMapping("/mx-to-mt")
    public ResponseEntity<String> convertMXtoMT(
            @RequestParam("file") MultipartFile file
    ) {
        String mxMessage = mtconventerService.convert(file);
        return ResponseEntity.ok(mxMessage);
    }
}