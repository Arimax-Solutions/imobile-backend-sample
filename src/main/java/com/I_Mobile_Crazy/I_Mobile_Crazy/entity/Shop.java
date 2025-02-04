package com.I_Mobile_Crazy.I_Mobile_Crazy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long shop_id;
    private String shop_name;
    private String address;
    private String email;
    private String contact_number;
    private String owner_nic;
    private double credit_limit;
    private boolean is_deleted;

    @OneToMany(mappedBy = "shop")
    private List<ReturnPhones> returnPhones;
}
