package com.I_Mobile_Crazy.I_Mobile_Crazy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Author : Chanuka Weerakkody
 * Since : 20.1.1
 **/
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnOrder {
    @Id
    private String return_order_id;

    private double discount;
    private double actual_price;
    private double total_amount;
    private Date date;
    private boolean is_deleted;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToMany
    @JoinTable(
            name = "return_order_imei",
            joinColumns = @JoinColumn(name = "return_order_id"),
            inverseJoinColumns = @JoinColumn(name = "imei_id")
    )
    private List<ReturnPhones> returnPhones;
}
