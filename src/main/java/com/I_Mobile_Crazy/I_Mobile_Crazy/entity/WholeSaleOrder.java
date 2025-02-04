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
public class WholeSaleOrder {
    @Id
    private String wholesale_order_id;

    private double discount;
    private double actual_price;
    private double total_amount;
    private Date date;
    private String status;
    private boolean is_deleted;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToMany
    @JoinTable(
            name = "wholesale_order_item",
            joinColumns = @JoinColumn(name = "wholesale_order_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items;

    @ManyToMany
    @JoinTable(
            name = "wholesale_order_imei",
            joinColumns = @JoinColumn(name = "wholesale_order_id"),
            inverseJoinColumns = @JoinColumn(name = "imei_id")
    )
    private List<IMEI> imeis;
}
