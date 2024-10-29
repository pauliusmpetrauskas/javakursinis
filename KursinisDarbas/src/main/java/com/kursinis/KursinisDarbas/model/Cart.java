package com.kursinis.KursinisDarbas.model;

import jakarta.persistence.*;
import javafx.collections.ObservableList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.time.LocalDate;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private LocalDate dateCreated;
    @OneToMany(mappedBy = "", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Product> itemsInCart;

    public Cart(ObservableList<Product> itemsInCart) {
        this.dateCreated = LocalDate.now();;
        this.itemsInCart = itemsInCart;
    }


    @Override
    public String toString() {
        return "ORDER NUMBER: " + id +3 ; }

}
