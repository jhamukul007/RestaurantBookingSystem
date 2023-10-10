package com.rbs.restaurantbookingsystem.models;

import com.rbs.restaurantbookingsystem.enums.CuisineType;
import com.rbs.restaurantbookingsystem.enums.RestaurantStatus;
import com.rbs.restaurantbookingsystem.enums.RestaurantType;
import com.rbs.restaurantbookingsystem.exceptions.CuisineAlreadyExistException;
import com.sun.source.tree.Tree;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

@Builder
@Getter
@Setter
@ToString
public class Restaurant extends BaseEntity {
    private String name;
    private Location location;
    private BigDecimal costForTwo;
    private RestaurantType restaurantType;
    // current state of restaurant
    private RestaurantStatus status;
    private Map<CuisineType, Cuisine> cuisineTypeMap;

    private Map<String, TreeSet<RestaurantSlot>> dateSlotMap;

    public Map<CuisineType, Cuisine> getCuisines() {
        if (cuisineTypeMap == null) cuisineTypeMap = new HashMap<>();
        return cuisineTypeMap;
    }

    public Map<String, TreeSet<RestaurantSlot>> getSlotMap() {
        if (dateSlotMap == null) dateSlotMap = new HashMap<>();
        return dateSlotMap;
    }

    public void addCuisine(Cuisine cuisine) {
        if (getCuisines().containsKey(cuisine.getCuisineType()))
            throw new CuisineAlreadyExistException();
        cuisineTypeMap.put(cuisine.getCuisineType(), cuisine);
    }

    public void addSlot(String date, List<RestaurantSlot> slots) {
        TreeSet<RestaurantSlot> alreadyAvailableSlots = getSlotMap().getOrDefault(date, new TreeSet<>(Comparator.comparing(RestaurantSlot::getFrom)));
        slots.forEach(slot -> {
            alreadyAvailableSlots.add(slot);
        });
        getSlotMap().put(date, alreadyAvailableSlots);
    }

    public void getAvailableSlotForDay(String date){
        TreeSet<RestaurantSlot> alreadyAvailableSlots =  getSlotMap().get(date);
    }

}
