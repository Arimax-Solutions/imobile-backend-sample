package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.UserDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.User;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.SuperService;


/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/

public interface UserService extends SuperService<User,Integer> {
    Boolean existsByUsername(String username);
}
