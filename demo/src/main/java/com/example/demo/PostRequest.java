package com.example.demo;

import java.util.List;

public record PostRequest(
                String content, // text content
                List<String> tags, // tag names
                String visibility // visibility setting
) {
}
