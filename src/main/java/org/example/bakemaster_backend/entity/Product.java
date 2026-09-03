package org.example.bakemaster_backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String category; // CAKE, BREAD, PASTRY, COOKIE, CUPCAKE

    @Column(nullable = false)
    private double sellingPrice;

    private double costPrice; // Cost of Goods Sold (COGS) calculated from recipe

    private int shelfLifeDays; // Standard shelf life window (e.g. 2 days for fresh cake, 7 for cookies)

    @Column(length = 1000)
    private String description;

    private String imageUrl;

    private boolean active = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RecipeItem> recipeItems = new ArrayList<>();
}
