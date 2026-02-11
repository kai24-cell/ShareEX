package com.example.demo;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

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
            @RequestPart(value = "file", required = false) MultipartFile file,
            HttpSession session) {
        // received data print to console(for debugging)
        System.out.println("received text: " + request.content());
        User currentUser = (User) session.getAttribute("currentUser");
        if (file != null) {
            System.out.println("received file name: " + file.getOriginalFilename());
        }
        if (currentUser != null) {
            System.out.println("Post created by user: " + currentUser.getUsername());
        } else {
            System.out.println("Posting as Guest");
        }

        postService.savePost(request, file, currentUser);

        return ResponseEntity.ok("Post created successfully");
    }

    @GetMapping("/search")
    public List<Post> searchPosts(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tag) {
        if (tag != null) {
            return postService.searchByTag(tag);
        } else if (keyword != null) {
            return postService.searchByKeyword(keyword);
        }
        return postService.getAllPosts();
    }

    @GetMapping("/related")
    public List<Post> getRelatedPosts(@RequestParam long id) {
        return postService.getRelatedPosts(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        try {
            postService.deletePost(id, currentUser);
            return ResponseEntity.ok("Post deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}