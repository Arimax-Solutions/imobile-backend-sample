package com.I_Mobile_Crazy.I_Mobile_Crazy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetailOrderDTO {
    private String retail_order_id;
    private double discount;
    private double actual_price;
    private double total_amount;
    private Date date;
    private String warranty;
    private boolean is_deleted;
    private CustomerDTO customer;
    private List<ItemDTO> items;
    private List<IMEIDTO> imeis;

}
