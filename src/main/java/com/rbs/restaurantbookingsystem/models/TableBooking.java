package com.rbs.restaurantbookingsystem.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TableBooking extends BaseEntity {
    private Table table;
    private String date;
    private RestaurantSlot restaurantSlot;
}
