package com.rbs.restaurantbookingsystem.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class User extends BaseEntity {
    private String name;
    private Long phone;
    private List<TableBooking> tableBookings;

    public User(String name, Long phone) {
        this.name = name;
        this.phone = phone;
        this.tableBookings = new ArrayList<>();
    }

    public void addBooking(TableBooking tableBooking) {
        this.tableBookings.add(tableBooking);
    }

}
