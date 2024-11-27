package com.example.events.category;

import com.example.events.configs.RateLimitingService;
import com.example.events.event.EventRequest;
import com.example.events.event.EventResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final RateLimitingService rateLimitingService;

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
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable UUID categoryId) {
        categoryService.deleteCategory(categoryId);

        return  ResponseEntity.ok("Event Deleted successfully" );
    }

    @PutMapping("/update/{categoryId}")
    public ResponseEntity<CategoryResponse> updateEvent(@PathVariable UUID categoryId, @RequestBody CategoryRequest  categoryRequest){
        return ResponseEntity.ok(categoryService.updateCategory(categoryId,categoryRequest));
    }

    @GetMapping("/events/{categoryName}")
    public ResponseEntity<List<CategoryEventResponse>> filterEventsByCategory(
            @PathVariable String categoryName,
            HttpServletRequest request
    ) {
        // Check if the request is within the rate limit
        rateLimitingService.checkRateLimit(request);

        // Fetch events within the date range
        CategoryEventResponse allEvents = categoryService.filterEventByCategoryName(categoryName);
        // If no events found, return 204 No Content
        if (allEvents == null) {
            return ResponseEntity.noContent().build();
        }

        // Return the list of events with 200 OK
        return ResponseEntity.ok(Collections.singletonList(allEvents));
    }




}
