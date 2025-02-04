package com.I_Mobile_Crazy.I_Mobile_Crazy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long item_id;
    private String category;
    private String brand;
    private String name;
    private String colour;
    private String warranty_period;
    private int qty;
    private double price;
    private boolean is_deleted;

    @ManyToMany(mappedBy = "items")
    private List<RetailOrder> retailOrders;

}
