package com.I_Mobile_Crazy.I_Mobile_Crazy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IMEI {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Models model;

    private String imei;

    private String storage;

    private String colour;

    private String warranty;

    @Column(name = "ios_version")
    private double iOSVersion;

    @Column(name = "battery_health")
    private double batteryHealth;

    private double price;

    @Column(columnDefinition = "VARCHAR(255) default 'sale'")
    private String status = "sale";

    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted;

    @ManyToMany(mappedBy = "imeis")
    private List<RetailOrder> retailOrders;

    @ManyToMany(mappedBy = "imeis")
    private List<WholeSaleOrder> wholesaleOrders;

}
