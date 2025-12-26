package com.example.demo;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_name", nullable = true, unique = true) // only change made
    private String tagName;

    @ManyToMany(mappedBy = "tags")
    private List<Post> posts;

    // constructors
    public Tag() {
    }

    public Tag(String tagName) {
        this.tagName = tagName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    // No setter for id since it's auto-generated
    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}