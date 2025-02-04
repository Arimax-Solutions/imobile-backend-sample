package com.I_Mobile_Crazy.I_Mobile_Crazy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private Long item_id;
    private String category;
    private String brand;
    private String name;
    private String colour;
    private String warranty_period;
    private int qty;
    private double price;
    private boolean is_deleted;
}
