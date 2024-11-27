package com.example.events.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<EventCategory, UUID> {
   Optional<EventCategory>  findByCategoryName(String categoryName);
   @Query("SELECT c FROM EventCategory c WHERE LOWER(TRIM(c.categoryName)) = LOWER(TRIM(:categoryName))")
   Optional<EventCategory> findByCategoryNameIgnoringCaseAndSpaces(@Param("categoryName") String categoryName);
}
