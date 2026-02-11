package com.example.demo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByContentContainingAndVisibility(String keyword, String visibility);// free word search

    List<Post> findByTags_TagNameAndVisibility(String tagName, String visibility);// search by tag

    List<Post> findDistinctByTagsInAndVisibility(List<Tag> tags, String visibility);// search by multiple tags
    // AndVisibility have meaning. It means filtering by visibility as well.
}
