package com.I_Mobile_Crazy.I_Mobile_Crazy.repo;

import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Models;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/

public interface ModelRepository extends JpaRepository<Models, Long> {
    List<Models> findByStockId(Long stockId);
}
