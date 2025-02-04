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
public class UserDTO {
    public String name;
    public String role;
    public int contact_number;

    public String email;
    public String username;
    public String password;
    public boolean is_deleted;
}
