package org.example.traveldesktopapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "language")
    private String language;

    @Column(name = "localization")
    private String localization;

    @Column(name = "country")
    private String country;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "destination")
    private String destination;

    @Column(name = "price")
    private double price;

    @Column(name = "currency")
    private String currency;

    public Offer(Offer other) {
        this.id = other.id;
        this.language = other.language;
        this.localization = other.localization;
        this.country = other.country;
        this.startDate = other.startDate;
        this.endDate = other.endDate;
        this.destination = other.destination;
        this.price = other.price;
        this.currency = other.currency;
    }

    public String toString(){
        return country + " " + startDate + " " + endDate + " " + destination + " " + price + " " + currency;
    }
}
