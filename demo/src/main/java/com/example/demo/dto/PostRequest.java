package com.example.demo.dto;

import java.util.List;

public record PostRequest(
        String content, // text content
        List<String> tags, // tag names
        String visibility // visibility setting
) {
}
