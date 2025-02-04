package com.I_Mobile_Crazy.I_Mobile_Crazy.repo;

import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.IMEI;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.ReturnPhones;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Repository
public interface ReturnPhonesRepository extends JpaRepository<ReturnPhones,Long> {
    List<ReturnPhones> findByImeiIn(List<String> imeiNumbers);
    Optional<ReturnPhones> findByImei(String imei);
}

