package com.example.demo.service;

import com.example.demo.dto.RecipeRequest;
import com.example.demo.model.Recipe;
import com.example.demo.model.User;
import com.example.demo.repository.RecipeRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id: " + id));
    }

    public List<Recipe> getRecipesByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));
        return recipeRepository.findByAuthor(user);
    }

    public List<Recipe> searchRecipes(String query) {
        return recipeRepository.findByTitleContainingIgnoreCase(query);
    }

    public Recipe createRecipe(RecipeRequest request, String userEmail) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Recipe recipe = new Recipe();
        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setIngredients(request.getIngredients());
        recipe.setInstructions(request.getInstructions());
        recipe.setAuthor(author);

        return recipeRepository.save(recipe);
    }

    public Recipe updateRecipe(Long id, RecipeRequest request, String userEmail) {
        Recipe recipe = getRecipeById(id);
        
        // Verify the user is the author
        if (!recipe.getAuthor().getEmail().equals(userEmail)) {
            throw new RuntimeException("You can only update your own recipes");
        }

        recipe.setTitle(request.getTitle());
        recipe.setDescription(request.getDescription());
        recipe.setIngredients(request.getIngredients());
        recipe.setInstructions(request.getInstructions());

        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(Long id, String userEmail) {
        Recipe recipe = getRecipeById(id);
        
        // Verify the user is the author
        if (!recipe.getAuthor().getEmail().equals(userEmail)) {
            throw new RuntimeException("You can only delete your own recipes");
        }

        recipeRepository.delete(recipe);
    }

    // ADMIN can delete any recipe
    public void deleteRecipeById(Long id) {
        Recipe recipe = getRecipeById(id);
        recipeRepository.delete(recipe);
    }
}
