package com.example.demo;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/posts")
public class ApiController {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createPost(
            @RequestPart("data") PostRequest request,
            @RequestPart("file") MultipartFile file) {
        // 受け取ったデータを処理するロジックをここに実装
        System.out.println("受け取ったテキスト: " + request.content());
        System.out.println("受け取ったファイル名: " + file.getOriginalFilename());

        return ResponseEntity.ok("Post created successfully");
    }
}