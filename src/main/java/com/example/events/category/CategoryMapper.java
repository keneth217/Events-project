package com.example.events.category;

import org.springframework.stereotype.Service;

@Service
public class CategoryMapper {
    public CategoryResponse toCategory(EventCategory eventCategory) {
        CategoryResponse categoryResponse=CategoryResponse.builder()
                .id(eventCategory.getId())
                .categoryDescription(eventCategory.getCategoryDescription())
                .categoryName(eventCategory.getCategoryName())
                .build();
        return categoryResponse;
    }
}
