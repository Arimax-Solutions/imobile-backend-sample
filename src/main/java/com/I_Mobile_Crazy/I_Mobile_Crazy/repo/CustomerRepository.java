package com.I_Mobile_Crazy.I_Mobile_Crazy.repo;

import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
