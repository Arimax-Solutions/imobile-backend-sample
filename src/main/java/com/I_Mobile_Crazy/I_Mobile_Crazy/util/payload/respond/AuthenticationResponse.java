package com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.respond;

import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private User authenticatedUser;
}
