package com.example.events.category;

import com.example.events.event.EventRequest;
import com.example.events.event.MyEvent;
import com.example.events.exceptions.EventNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private  final CategoryMapper mapper;
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        EventCategory category= EventCategory.builder()
                .categoryName(categoryRequest.getCategoryName())
                .categoryDescription(categoryRequest.getCategoryDescription())

                .build();
        EventCategory savedCategory=categoryRepository.save(category);
        return mapper.toCategory(savedCategory);
    }
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository
                .findAll()
                .stream()
                .map(mapper::toCategory)
                .collect(Collectors.toList());
    }
    public CategoryResponse getCategoryById(UUID categoryId) {
        EventCategory category= categoryRepository.findById(categoryId).
                orElseThrow(()-> new EventNotFoundException("Category Not found with the given id"+ categoryId));

        return mapper.toCategory(category);
    }
    public String deleteCategory(UUID categoryId) {
        EventCategory category= categoryRepository.findById(categoryId).
                orElseThrow(()-> new EventNotFoundException("Category Not found with the given id"+ categoryId));
    categoryRepository.delete(category);
        return "Category deleted Successfully";
    }
    public CategoryResponse updateCategory(UUID categoryId, CategoryRequest categoryRequest) {
        EventCategory category= categoryRepository.findById(categoryId).
                orElseThrow(()-> new EventNotFoundException("Category Not found with the given id"+ categoryId));
category.setCategoryDescription(categoryRequest.getCategoryDescription());
category.setCategoryName(categoryRequest.getCategoryName());
EventCategory savedCategory= categoryRepository.save(category);
        return mapper.toCategory(savedCategory);
    }
}
