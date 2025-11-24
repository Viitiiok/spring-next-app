package com.example.demo.service;

import com.example.demo.dto.CommentDto;
import com.example.demo.model.Comment;
import com.example.demo.model.Recipe;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.RecipeRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository,
                          RecipeRepository recipeRepository,
                          UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    // GET comments for recipe
    public List<CommentDto> findByRecipe(Long recipeId) {
        return commentRepository.findByRecipe_Id(recipeId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // ADD comment
    public CommentDto addComment(Long recipeId, CommentDto dto) {

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id = " + recipeId));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id = " + dto.getUserId()));

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setRecipe(recipe);
        comment.setUser(user);

        Comment saved = commentRepository.save(comment);
        return mapToDto(saved);
    }

    // DELETE comment
    public void deleteById(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("Comment not found with id = " + commentId);
        }
        commentRepository.deleteById(commentId);
    }

    // ------------ MAPPERS ------------

    private CommentDto mapToDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .recipeId(comment.getRecipe().getId())
                .userId(comment.getUser().getId())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
