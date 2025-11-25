package com.example.demo.controller;

import com.example.demo.dto.CommentRequest;
import com.example.demo.model.Comment;
import com.example.demo.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes/{recipeId}/comments")
@Tag(name = "Comments", description = "Comment management endpoints (requires USER or ADMIN role)")
@SecurityRequirement(name = "Bearer Authentication")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    @Operation(summary = "Get comments for a recipe", description = "Retrieve all comments for a specific recipe")
    public ResponseEntity<List<Comment>> getCommentsByRecipe(@PathVariable Long recipeId) {
        return ResponseEntity.ok(commentService.getCommentsByRecipe(recipeId));
    }

    @PostMapping
    @Operation(summary = "Add a comment to a recipe", description = "Create a new comment on a recipe (authenticated user becomes the author)")
    public ResponseEntity<Comment> createComment(
            @PathVariable Long recipeId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Comment created = commentService.createComment(recipeId, request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete a comment", description = "Delete a comment (only the author can delete their own comments)")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long recipeId,
            @PathVariable Long commentId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        commentService.deleteComment(commentId, userEmail);
        return ResponseEntity.noContent().build();
    }
}
