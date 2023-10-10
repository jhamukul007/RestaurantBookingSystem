package com.rbs.restaurantbookingsystem.managers;

import com.rbs.restaurantbookingsystem.enums.CuisineType;
import com.rbs.restaurantbookingsystem.enums.RestaurantStatus;
import com.rbs.restaurantbookingsystem.enums.RestaurantType;
import com.rbs.restaurantbookingsystem.enums.SlotStatus;
import com.rbs.restaurantbookingsystem.exceptions.RestaurantNotFoundException;
import com.rbs.restaurantbookingsystem.logging.Logger;
import com.rbs.restaurantbookingsystem.models.Cuisine;
import com.rbs.restaurantbookingsystem.models.Location;
import com.rbs.restaurantbookingsystem.models.Restaurant;
import com.rbs.restaurantbookingsystem.models.RestaurantSlot;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class RestaurantManager {
    private List<Restaurant> restaurantList;
    private static RestaurantManager restaurantManager;
    private final CuisineManager cuisineManager;

    private final Logger logger;

    private RestaurantManager(CuisineManager cuisineManager, Logger logger) {
        this.cuisineManager = cuisineManager;
        this.logger = logger;
        this.restaurantList = new ArrayList<>();
    }

    public static RestaurantManager getIns(CuisineManager cuisineManager, Logger logger, LockManager lockManager) {
        if (restaurantManager == null) {
            synchronized (RestaurantManager.class) {
                if (restaurantManager == null)
                    restaurantManager = new RestaurantManager(cuisineManager, logger);
            }
        }
        return restaurantManager;
    }

    public Restaurant registerRestaurant(@NonNull String name, @NonNull String city, @NonNull String area, @NonNull RestaurantType restaurantType, BigDecimal costForTwo) {
        Location location = Location.builder()
                .area(area)
                .city(city)
                .build();
        Restaurant restaurant = Restaurant.builder()
                .location(location)
                .name(name)
                .restaurantType(restaurantType)
                .status(RestaurantStatus.NOT_ACTIVE)
                .costForTwo(costForTwo)
                .build();
        restaurantList.add(restaurant);
        return restaurant;
    }

    public List<Restaurant> searchRestaurantByName(String name) {
        return restaurantList.stream().filter(restaurant -> Objects.deepEquals(name, restaurant.getName()) && isActiveRestaurant(restaurant)).collect(Collectors.toList());
    }

    public List<Restaurant> searchByCity(String cityName) {
        return restaurantList.stream().filter(restaurant -> Objects.deepEquals(cityName, restaurant.getLocation().getCity()) && isActiveRestaurant(restaurant)).collect(Collectors.toList());
    }

    public List<Restaurant> searchByArea(String area) {
        return restaurantList.stream().filter(restaurant -> Objects.deepEquals(area, restaurant.getLocation().getArea()) && isActiveRestaurant(restaurant)).collect(Collectors.toList());
    }

    public List<Restaurant> searchByType(RestaurantType type) {
        return restaurantList.stream().filter(restaurant -> Objects.deepEquals(type, restaurant.getRestaurantType()) && isActiveRestaurant(restaurant)).collect(Collectors.toList());
    }

    public List<Restaurant> searchByCostForTwo(BigDecimal costForTwo) {
        return restaurantList.stream().filter(restaurant -> Objects.deepEquals(costForTwo, restaurant.getCostForTwo()) && isActiveRestaurant(restaurant)).collect(Collectors.toList());
    }


    public List<Restaurant> searchByCuisine(CuisineType cuisineType) {
        return restaurantList.stream().filter(restaurant -> restaurant.getCuisines().get(cuisineType) != null && isActiveRestaurant(restaurant)).collect(Collectors.toList());
    }


    private boolean isActiveRestaurant(Restaurant restaurant) {
        return Objects.deepEquals(restaurant.getStatus(), RestaurantStatus.ACTIVE);
    }

    public void markActiveRestaurant(String restaurantId) {
        Restaurant restaurant = getOrThrowNotFound(restaurantId);
        if (RestaurantStatus.ACTIVE.equals(restaurant.getStatus()))
            throw new IllegalArgumentException("Restaurant is already active");
        restaurant.setStatus(RestaurantStatus.ACTIVE);
    }

    public Optional<Restaurant> getById(String id) {
        return restaurantList.stream().filter(restaurant -> Objects.deepEquals(id, restaurant.getId())).findFirst();
    }

    public Restaurant getOrThrowNotFound(String id) {
        return getById(id).orElseThrow(() -> new RestaurantNotFoundException());
    }

    public void addCuisine(String restaurantId, List<CuisineType> cuisineTypes) {
        Restaurant restaurant = getOrThrowNotFound(restaurantId);
        cuisineTypes.forEach(cuisineType -> {
            Cuisine cuisine = cuisineManager.getOrPut(cuisineType);
            restaurant.addCuisine(cuisine);
        });
    }

    public void addSlot(String restaurantId, String date, List<RestaurantSlot> restaurantSlots) {
        Restaurant restaurant = getOrThrowNotFound(restaurantId);
        restaurant.addSlot(date, restaurantSlots);
    }

    public void getAvailableSlotForRestaurant(String restaurantId, String date) {
        Restaurant restaurant = getOrThrowNotFound(restaurantId);
        TreeSet<RestaurantSlot> slots = restaurant.getDateSlotMap().get(date);
        List<RestaurantSlot> availableSlots = slots.stream().filter(slot -> Objects.deepEquals(SlotStatus.AVAILABLE, slot.getSlotStatus()) && slot.getAvailableTable().size() > 0).collect(Collectors.toList());
        availableSlots.forEach(slot -> {
            logger.log(String.format("Slot :: time from :: %s to time :: %s on date %s available in restaurant %s ", slot.getFrom(), slot.getTo(), date, restaurant.getName()));
        });
    }
}
