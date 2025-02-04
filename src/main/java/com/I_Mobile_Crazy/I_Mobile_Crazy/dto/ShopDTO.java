package com.I_Mobile_Crazy.I_Mobile_Crazy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShopDTO {
    private Long shop_id;
    private String shop_name;
    private String address;
    private String email;
    private String contact_number;
    //private double outStanding;
    private String owner_nic;
    private double credit_limit;
    private boolean is_deleted;
    private Double outstanding;

}
