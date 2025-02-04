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
public class CustomerDTO {
    private Long customer_id;
    private String name;
    private String email;
    private String contact_phone;
    private String nic;
    private boolean is_deleted;
    private Double outstandingAmount;
}
