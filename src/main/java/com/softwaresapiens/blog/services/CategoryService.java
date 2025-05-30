package com.softwaresapiens.blog.services;

import com.softwaresapiens.blog.domain.entities.Category;
import com.softwaresapiens.blog.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> listCategories(){
        return categoryRepository.findAllWithPostCount();
    }

    @Transactional
    public Category createCategory(Category category) {
        if(categoryRepository.existsByNameIgnoreCase(category.getName())){
            throw new IllegalArgumentException("Category already exists with name: " + category.getName());
        }
        return categoryRepository.save(category);
    }

    public void deleteCategory(UUID id){
        Optional<Category> category = categoryRepository.findById(id);
        if(category.isPresent()){
            if(category.get().getPosts().size() > 0){
                throw new IllegalArgumentException("Category has posts associated with it: " + id);
            }
        }
        categoryRepository.deleteById(id);
    }
}
