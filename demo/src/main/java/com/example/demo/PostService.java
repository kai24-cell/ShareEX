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
    public void savePost(PostRequest request, MultipartFile file, User user) {
        Post post = new Post();
        post.setContent(request.content());
        post.setVisibility(request.visibility());
        post.setUser(user);
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

    public void deletePost(Long postId, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }
        Long postUserId = post.getUser() != null ? post.getUser().getId() : null;
        Long currentUserId = currentUser.getId();

        System.out.println("postUserId: " + postUserId);
        System.out.println("currentUserId: " + currentUserId);

        boolean isAdmin = "ADMIN".equals(currentUser.getRole());
        boolean isOwner = postUserId != null && postUserId.equals(currentUserId);

        if (isAdmin || isOwner) {
            postRepository.delete(post);
        } else {
            throw new RuntimeException("You do not have permission to delete this post");
        }
    }

    // Free word search
    public List<Post> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return postRepository.findAll();
        }
        return postRepository.findByContentContainingAndVisibility(keyword, "public");
    }

    // Search by tag
    public List<Post> searchByTag(String tagName) {
        return postRepository.findByTags_TagNameAndVisibility(tagName, "public");
    }

    // Get related posts based on shared tags
    public List<Post> getRelatedPosts(Long targetPostId) {
        Post currentPost = postRepository.findById(targetPostId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        List<Tag> tags = currentPost.getTags();
        if (tags.isEmpty()) {
            return new ArrayList<>();
        }
        List<Post> relatedPosts = postRepository.findDistinctByTagsInAndVisibility(tags, "public");
        return relatedPosts.stream()
                .filter(post -> !post.getId().equals(targetPostId)) // Exclude the current post
                .toList();
    }

    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
}
