package com.example.events.category;

import com.example.events.event.EventRequest;
import com.example.events.event.EventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest){
        return ResponseEntity.ok(categoryService.createCategory(categoryRequest));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllEvents() {
        List <CategoryResponse> allCategories = categoryService.getAllCategories();
        if (allCategories == null || allCategories.isEmpty()) {
            // Return 204 No Content for an empty list
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(allCategories);
    }
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getEvent(@PathVariable UUID categoryId) {
        var category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            // Return 404 Not Found for a missing event
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);

        return  ResponseEntity.ok("Event Deleted successfully" );
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<CategoryResponse> updateEvent(@PathVariable UUID categoryId, @RequestBody CategoryRequest  categoryRequest){
        return ResponseEntity.ok(categoryService.updateCategory(categoryId,categoryRequest));
    }


}
