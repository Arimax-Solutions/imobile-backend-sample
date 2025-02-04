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
public class ReturnPhonesDTO {
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
    private Long customer_id;
    private Long shop_id;
}
