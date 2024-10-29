package com.kursinis.KursinisDarbas.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fertilizer extends Product {
    private double weight;
    private String chemicalDescription;

    public Fertilizer(String title, String description, String manufacturer, Warehouse warehouse, String weight, String chemicalDescription) {
        super(title, description, manufacturer, warehouse);
        this.weight = Double.parseDouble(weight);
        this.chemicalDescription = chemicalDescription;
    }

}
