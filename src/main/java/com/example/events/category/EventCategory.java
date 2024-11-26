package com.example.events.category;


import com.example.events.event.MyEvent;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "category")
public class EventCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)  // Use Identity if you are manually generating UUID
    private UUID  id; // Generate UUID on the fly
    private String categoryName;
    private String categoryDescription;


    @OneToMany(mappedBy = "category") // mappedBy indicates that 'category' field in Event is the owning side
    private List<MyEvent> events = new ArrayList<>();


}
