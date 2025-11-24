package com.example.demo.controller;

import com.example.demo.dto.CommentDto;
import com.example.demo.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes/{recipeId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // GET /api/recipes/{recipeId}/comments
    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Long recipeId) {
        return ResponseEntity.ok(commentService.findByRecipe(recipeId));
    }

    // POST /api/recipes/{recipeId}/comments
    @PostMapping
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long recipeId,
            @RequestBody CommentDto commentDto
    ) {
        return ResponseEntity.ok(commentService.addComment(recipeId, commentDto));
    }

    // DELETE /api/recipes/{recipeId}/comments/{commentId}
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteById(commentId);
        return ResponseEntity.noContent().build();
    }
}
