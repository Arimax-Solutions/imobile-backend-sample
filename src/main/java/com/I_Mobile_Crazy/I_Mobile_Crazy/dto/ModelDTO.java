package com.I_Mobile_Crazy.I_Mobile_Crazy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelDTO {
    private Long id;
    private String name;
    private List<IMEIDTO> imeiNumbers;
    private String stockAddedDate;
}
