package org.example.restaurantservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;
    private String description;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    @JsonIgnore // Da se ne ode u beskonačnu petlju prilikom prikaza
    private Restaurant restaurant;
}