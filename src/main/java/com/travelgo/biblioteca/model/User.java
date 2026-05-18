package com.travelgo.biblioteca.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_users") 
@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 
    
    private String username;
    private String email;
}