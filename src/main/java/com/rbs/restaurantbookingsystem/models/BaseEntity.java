package com.rbs.restaurantbookingsystem.models;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BaseEntity {
    private String id;

    public BaseEntity() {
        this.id = UUID.randomUUID().toString();
    }
}
