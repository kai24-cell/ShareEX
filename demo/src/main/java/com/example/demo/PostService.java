package com.example.demo;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import jakarta.transaction.Transactional;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    private final Path imageStorageLocation = Paths.get("uploads");

    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        try {
            Files.createDirectories(this.imageStorageLocation);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create image storage directory", e);
        }
    }

    @Transactional // 0 or 100
    public void savePost(PostRequest request, MultipartFile file) {
        Post post = new Post();
        post.setContent(request.content());
        post.setVisibility(request.visibility());
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            try {
                Path targetLocation = this.imageStorageLocation.resolve(storedFilename);
                Files.copy(file.getInputStream(), targetLocation);

                post.setImageUrl("/uploads/" + storedFilename);
            } catch (Exception e) {
                throw new RuntimeException("Failed to store image file", e);
            }
        }

        List<Tag> tagList = new ArrayList<>();

        if (request.tags() != null) {
            for (String tagName : request.tags()) {
                String CleanName = tagName.trim();
                if (CleanName.isEmpty()) {
                    continue; // empty tag names are skipped
                }
                Tag tag = tagRepository.findByTagName(CleanName)// if tag exists, use it; otherwise, create a new one
                        .orElseGet(() -> new Tag(CleanName));
                tagList.add(tag);
            }
        }
        post.setTags(tagList);
        postRepository.save(post);
        System.out.println("succeeded save DB");
    }

}
