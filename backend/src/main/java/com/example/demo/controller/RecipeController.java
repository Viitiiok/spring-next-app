package com.example.demo.controller;

import com.example.demo.dto.RecipeDto;
import com.example.demo.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // GET /api/recipes
    @GetMapping
    public ResponseEntity<List<RecipeDto>> getAllRecipes() {
        return ResponseEntity.ok(recipeService.findAll());
    }

    // GET /api/recipes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable Long id) {
        return ResponseEntity.ok(recipeService.findById(id));
    }

    // POST /api/recipes
    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe(@RequestBody RecipeDto recipeDto) {
        RecipeDto created = recipeService.save(recipeDto);
        return ResponseEntity.ok(created);
    }

    // PUT /api/recipes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<RecipeDto> updateRecipe(
            @PathVariable Long id,
            @RequestBody RecipeDto recipeDto
    ) {
        RecipeDto updated = recipeService.update(id, recipeDto);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/recipes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/recipes/search?title=...
    @GetMapping("/search")
    public ResponseEntity<List<RecipeDto>> searchRecipes(@RequestParam String title) {
        return ResponseEntity.ok(recipeService.searchByTitle(title));
    }

    // GET /api/recipes/user/{userId}
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecipeDto>> getRecipesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(recipeService.findByUser(userId));
    }
}
