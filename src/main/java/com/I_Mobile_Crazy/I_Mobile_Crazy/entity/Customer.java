package com.I_Mobile_Crazy.I_Mobile_Crazy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customer_id;

    private String name;
    private String email;
    private String contact_phone;
    private String nic;
    private boolean is_deleted;

    @OneToMany(mappedBy = "customer")
    private List<RetailOrder> retailOrders;

    @OneToMany(mappedBy = "customer")
    private List<ReturnPhones> returnPhones;

}
