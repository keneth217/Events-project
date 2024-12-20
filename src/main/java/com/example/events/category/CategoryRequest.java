package com.example.events.category;


import lombok.Builder;
import lombok.Data;
import java.util.UUID;
@Data
@Builder
public class CategoryRequest {
    private UUID id;
    private String categoryName;
    private String categoryDescription;
}
