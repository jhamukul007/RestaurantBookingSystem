package com.rbs.restaurantbookingsystem.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location extends BaseEntity{
    private String city;
    private String area;
}
