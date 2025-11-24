package com.example.demo.service;

import com.example.demo.dto.RecipeDto;
import com.example.demo.model.Recipe;
import com.example.demo.model.User;
import com.example.demo.repository.RecipeRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RecipeService(RecipeRepository recipeRepository,
                         UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    // GET ALL
    public List<RecipeDto> findAll() {
        return recipeRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // GET BY ID
    public RecipeDto findById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id = " + id));

        return mapToDto(recipe);
    }

    // CREATE
    public RecipeDto save(RecipeDto dto) {
        Recipe recipe = mapToEntity(dto);
        Recipe saved = recipeRepository.save(recipe);
        return mapToDto(saved);
    }

    // UPDATE
    public RecipeDto update(Long id, RecipeDto dto) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found with id = " + id));

        recipe.setTitle(dto.getTitle());
        recipe.setContent(dto.getContent());

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id = " + dto.getUserId()));
            recipe.setUser(user);
        }

        Recipe updated = recipeRepository.save(recipe);
        return mapToDto(updated);
    }

    // DELETE
    public void deleteById(Long id) {
        if (!recipeRepository.existsById(id)) {
            throw new RuntimeException("Recipe not found with id = " + id);
        }
        recipeRepository.deleteById(id);
    }

    // SEARCH BY TITLE
    public List<RecipeDto> searchByTitle(String title) {
        return recipeRepository.findByTitleContainingIgnoreCase(title)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // FIND BY USER
    public List<RecipeDto> findByUser(Long userId) {
        return recipeRepository.findByUser_Id(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // =====================
    // MAPPERS
    // =====================

    private RecipeDto mapToDto(Recipe recipe) {
        Long userId = recipe.getUser() != null ? recipe.getUser().getId() : null;

        return new RecipeDto(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getContent(),
                userId
        );
    }

    private Recipe mapToEntity(RecipeDto dto) {
        Recipe recipe = new Recipe();
        recipe.setId(dto.getId());
        recipe.setTitle(dto.getTitle());
        recipe.setContent(dto.getContent());

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id = " + dto.getUserId()));
            recipe.setUser(user);
        }

        return recipe;
    }
}
