package com.I_Mobile_Crazy.I_Mobile_Crazy.repo;

import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.IMEI;
import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Repository
public interface ItemRepository extends JpaRepository<Item,Long> {
    Optional<Item> findByName(String name);
}
