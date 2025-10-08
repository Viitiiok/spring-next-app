package com.example.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class RecipeDto {
    private Long id;
    private String title;
    private String content;
    private Long userId;
}
