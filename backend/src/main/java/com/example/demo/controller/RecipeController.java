package com.example.demo.controller;

import com.example.demo.dto.RecipeRequest;
import com.example.demo.model.Recipe;
import com.example.demo.service.RecipeService;
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
@RequestMapping("/api/recipes")
@Tag(name = "Recipes", description = "Recipe management (ADMIN creates/deletes, all can view)")
@SecurityRequirement(name = "Bearer Authentication")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping
    @Operation(summary = "Get all recipes", description = "Retrieve all recipes with their comments (accessible by USER and ADMIN)")
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.getAllRecipes());
    }

    @PostMapping
    @Operation(summary = "Create a new recipe", description = "Create a new recipe (ADMIN only)")
    public ResponseEntity<Recipe> createRecipe(
            @Valid @RequestBody RecipeRequest request,
            Authentication authentication) {
        String userEmail = authentication.getName();
        Recipe created = recipeService.createRecipe(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a recipe", description = "Delete a recipe by ID (ADMIN only)")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipeById(id);
        return ResponseEntity.noContent().build();
    }
}
