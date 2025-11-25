package com.example.demo.service;

import com.example.demo.dto.CommentRequest;
import com.example.demo.model.Comment;
import com.example.demo.model.Recipe;
import com.example.demo.model.User;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.RecipeRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Comment> getCommentsByRecipe(Long recipeId) {
        return commentRepository.findByRecipeId(recipeId);
    }

    public Comment createComment(Long recipeId, CommentRequest request, String userEmail) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + recipeId));

        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setRecipe(recipe);
        comment.setAuthor(author);

        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Allow deletion if: user is the author OR user is ADMIN
        boolean isAuthor = comment.getAuthor().getEmail().equals(userEmail);
        boolean isAdmin = user.getRole().getName().equals("ADMIN");

        if (!isAuthor && !isAdmin) {
            throw new RuntimeException("You can only delete your own comments unless you are an ADMIN");
        }

        commentRepository.delete(comment);
    }
}
