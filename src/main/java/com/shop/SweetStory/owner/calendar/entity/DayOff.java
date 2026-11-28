package com.shop.SweetStory.owner.calendar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "days_off")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DayOff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "date", nullable = false)
    LocalDate date;

}
