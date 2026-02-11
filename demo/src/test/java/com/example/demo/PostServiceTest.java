package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private PostService postService;

    @Test // Owner deleting their own post
    void testDeletePost_Success_Owner() {
        // Given
        Long postId = 1L;
        User owner = new User();
        owner.setId(100L);
        owner.setRole("USER");

        Post post = new Post();
        post.setId(postId);
        post.setUser(owner);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        postService.deletePost(postId, owner);
        verify(postRepository, times(1)).delete(post);
    }

    @Test // Admin deleting someone else's post
    void testDeletePost_Success_Admin() {
        Long postId = 1L;
        User owner = new User();
        owner.setId(200L);

        Post post = new Post();
        post.setId(postId);
        post.setUser(owner);

        User admin = new User();
        admin.setId(999L);
        admin.setRole("ADMIN");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        postService.deletePost(postId, admin);
        verify(postRepository, times(1)).delete(post);
    }

    @Test // Unauthorized user trying to delete someone else's post
    void testDeletePost_Failure_Unauthorized() {
        Long postId = 1L;
        User owner = new User();
        owner.setId(200L);

        Post post = new Post();
        post.setId(postId);
        post.setUser(owner);

        User otherUser = new User();
        otherUser.setId(300L);
        otherUser.setRole("USER");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(RuntimeException.class, () -> {
            postService.deletePost(postId, otherUser);
        });

        verify(postRepository, times(0)).delete(any());
    }
}
