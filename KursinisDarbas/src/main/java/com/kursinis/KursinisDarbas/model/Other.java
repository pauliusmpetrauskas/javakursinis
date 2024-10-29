package com.kursinis.KursinisDarbas.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Other extends Product {
    private int id;
    private double weight;

    public Other(String title, String description, String manufacturer, Warehouse warehouse, String weight) {
        super(title, description, manufacturer, warehouse);
        this.weight = Double.parseDouble(weight);
    }
}
