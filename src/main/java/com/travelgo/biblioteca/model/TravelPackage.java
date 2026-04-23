package com.travelgo.biblioteca.model; // Coincide con la carpeta 'model'

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
public class TravelPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    private Double price;
    private Integer durationDays;
    
    @ElementCollection
    private List<String> destinations; 
}