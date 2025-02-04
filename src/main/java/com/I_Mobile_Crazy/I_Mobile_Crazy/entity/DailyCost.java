package com.I_Mobile_Crazy.I_Mobile_Crazy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyCost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long daily_cost_id;
    private double amount;
    private String reason;
    private Date date;
}
