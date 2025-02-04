package com.I_Mobile_Crazy.I_Mobile_Crazy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Example: iPhones, Samsung Galaxy, etc.
    private String description; // Description of the stock
    private int qty; // Description of the stock

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Models> models = new ArrayList<>();

}
