package com.example.demo;

import java.util.List;

public record PostRequest(
                String content, // 試行錯誤メモ
                List<String> tags, // タグ
                String visibility // 公開範囲
) {
}