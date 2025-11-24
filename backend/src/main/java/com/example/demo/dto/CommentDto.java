package com.example.demo.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {

    private Long id;
    private String content;
    private Long recipeId;
    private Long userId;
    private OffsetDateTime createdAt;
}
