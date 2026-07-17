package com.ddtrinh.movie_booking.cinema.entity;

import com.ddtrinh.movie_booking.common.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cinemas")
public class Cinema extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String address;
}
