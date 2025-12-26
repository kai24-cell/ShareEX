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
    private final PostService postService;// receive PostService via constructor

    public ApiController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createPost(
            @RequestPart("data") PostRequest request,
            @RequestPart("file") MultipartFile file) {
        // received data print to console(for debugging)
        System.out.println("received text: " + request.content());
        System.out.println("received file name: " + file.getOriginalFilename());

        postService.savePost(request, file);

        return ResponseEntity.ok("Post created successfully");
    }
}