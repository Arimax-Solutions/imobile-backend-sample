package com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.respond;

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
@Builder
@Data
public class StandardResponse {
    private Integer status;
    private String message;
    private Object data;
}
