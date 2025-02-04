package com.I_Mobile_Crazy.I_Mobile_Crazy.repo;

import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Shop;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Repository
public interface ShopRepository extends JpaRepository<Shop,Long> {
}
