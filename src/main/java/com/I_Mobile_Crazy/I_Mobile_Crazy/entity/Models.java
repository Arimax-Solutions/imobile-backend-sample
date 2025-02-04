package com.I_Mobile_Crazy.I_Mobile_Crazy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Models {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

    private String name; // Example: iPhone 12 Pro, Samsung Galaxy S21, etc.

    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IMEI> imeiNumbers = new ArrayList<>();

    @Column(name = "stock_added_date")
    private String stockAddedDate;

    public void addIMEI(IMEI imei) {
        imeiNumbers.add(imei);
        imei.setModel(this);
    }
}
