package com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RegisterRequest {
    public String name;
    public int contact_number;
    public String email;
    public String username;
    public String password;
    public boolean is_deleted;
}
