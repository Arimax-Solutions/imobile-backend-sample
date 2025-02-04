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
public class ReturnItemDTO {
    private Long return_phone_id;
    public String category;
    public String brand;
    public String name;
    public String reason;
    public int contact_number;
    public boolean is_deleted;

}
