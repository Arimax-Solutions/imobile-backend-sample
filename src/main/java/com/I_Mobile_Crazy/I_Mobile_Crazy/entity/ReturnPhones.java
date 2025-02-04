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
public class ReturnPhones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long return_phone_id;

    private String imei;
    private String model;
    private String storage;
    private String contact_number;
    private Date date;
    private String colour;
    private String reason;
    private String name;
    private String status;
    private double outStanding;
    private boolean is_deleted;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToMany(mappedBy = "returnPhones")
    private List<ReturnOrder> returnOrders;
}
