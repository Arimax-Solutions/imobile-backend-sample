package com.I_Mobile_Crazy.I_Mobile_Crazy.repo;

import com.I_Mobile_Crazy.I_Mobile_Crazy.dto.IMEIDTO;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.IMEI;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Repository
public interface IMEIRepository extends JpaRepository<IMEI,Long> {
    IMEI findByImei(String imei);
    List<IMEI> findByImeiIn(List<String> imeis);
    @Transactional
    void deleteByModelId(Long modelId);

}
