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
public class RetailOrder {
    @Id
    private String retail_order_id;

    private double discount;
    private double actual_price;
    private double total_amount;
    private Date date;
    private boolean is_deleted;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToMany
    @JoinTable(
            name = "retail_order_item",
            joinColumns = @JoinColumn(name = "retail_order_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    private List<Item> items;

    @ManyToMany
    @JoinTable(
            name = "retail_order_imei",
            joinColumns = @JoinColumn(name = "retail_order_id"),
            inverseJoinColumns = @JoinColumn(name = "imei_id")
    )
    private List<IMEI> imeis;
}
