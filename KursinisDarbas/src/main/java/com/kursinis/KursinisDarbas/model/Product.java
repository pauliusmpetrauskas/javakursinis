package com.kursinis.KursinisDarbas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Product implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String title;
    String description;
    String manufacturer;
    @ManyToOne
    Warehouse warehouse;

    @ManyToOne
    Cart cart;

    public Product(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Product(String title, String description, String manufacturer) {
        this.title = title;
        this.description = description;
        this.manufacturer = manufacturer;
    }

    public Product(String title, String description, String manufacturer, Warehouse warehouse) {
        this.title = title;
        this.description = description;
        this.manufacturer = manufacturer;
        this.warehouse = warehouse;
    }

    @Override
    public String toString() {
        return title + " : " + warehouse;
    }
}
