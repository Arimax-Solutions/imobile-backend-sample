package com.I_Mobile_Crazy.I_Mobile_Crazy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IMEIDTO {
    private Long id;
    private String imei;
    private String storage;
    private String colour;
    private String warranty;
    private double iOSVersion;
    private double batteryHealth;
    private double price;
    private String status;
    private ModelDTO modelId;
    private boolean isDeleted;
    private CustomerDTO customer;
    private ShopDTO shop;
    private Date retailOrderDate;
    private Date wholesaleOrderDate;
}
