package com.rbs.restaurantbookingsystem.managers;

import com.rbs.restaurantbookingsystem.enums.CuisineType;
import com.rbs.restaurantbookingsystem.exceptions.CuisineAlreadyExistException;
import com.rbs.restaurantbookingsystem.models.Cuisine;

import java.util.HashMap;
import java.util.Map;

public class CuisineManager {
    private final Map<CuisineType, Cuisine> cuisineTypeCuisineMap;

    private static CuisineManager cuisineManager;

    private CuisineManager() {
        this.cuisineTypeCuisineMap = new HashMap<>();
    }

    public static CuisineManager getIns() {
        if (cuisineManager == null) {
            synchronized (CuisineManager.class) {
                if (cuisineManager == null)
                    cuisineManager = new CuisineManager();
            }
        }
        return cuisineManager;
    }

    private void addCuisine(CuisineType cuisineType, Cuisine cuisine) {
        if (cuisineTypeCuisineMap.containsKey(cuisineType))
            throw new CuisineAlreadyExistException();
        cuisineTypeCuisineMap.put(cuisineType, cuisine);
    }

    public Cuisine getOrPut(CuisineType cuisineType) {
        if(cuisineTypeCuisineMap.containsKey(cuisineType))
            return cuisineTypeCuisineMap.get(cuisineType);
        Cuisine cuisine = Cuisine.builder()
                .cuisineType(cuisineType)
                .build();
        addCuisine(cuisineType, cuisine);
        return cuisine;
    }
}
