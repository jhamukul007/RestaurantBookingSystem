package com.rbs.restaurantbookingsystem.models;

import com.rbs.restaurantbookingsystem.enums.CuisineType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Cuisine extends BaseEntity{
    private CuisineType cuisineType;
}
