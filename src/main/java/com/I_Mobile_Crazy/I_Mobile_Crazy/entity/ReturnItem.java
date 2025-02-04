package com.I_Mobile_Crazy.I_Mobile_Crazy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long return_phone_id;
    public String category;
    public String brand;
    public String name;
    public String reason;
    public int contact_number;
    public boolean is_deleted;
}
