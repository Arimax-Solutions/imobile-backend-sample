package com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.IMEIDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.IMEI;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.SuperService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/

public interface IMEIService extends SuperService<IMEIDTO, Long> {
    List<IMEIDTO> findAllSold();
}
