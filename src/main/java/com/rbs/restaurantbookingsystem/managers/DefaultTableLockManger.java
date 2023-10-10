package com.rbs.restaurantbookingsystem.managers;

import com.rbs.restaurantbookingsystem.exceptions.TableLockedException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultTableLockManger implements LockManager {
    // date -> restaurantId -> slotIdTime -> tables.
    private Map<String, Map<String, Map<String, List<String>>>> lockedTableDetails;

    public DefaultTableLockManger() {
        this.lockedTableDetails = new HashMap<>();
    }

    @Override
    public void lock(String date, String restaurantId, String slotId, String tableId) {
        Map<String, Map<String, List<String>>> lockedTableOnDate = lockedTableDetails.getOrDefault(date, new HashMap<>());
        Map<String, List<String>> lockedTableOnRestaurant = lockedTableOnDate.getOrDefault(restaurantId, new HashMap<>());
        List<String> lockedTable = lockedTableOnRestaurant.getOrDefault(slotId, new ArrayList<>());
        if (lockedTable.contains(tableId))
            throw new TableLockedException();
        else {
            lockedTable.add(tableId);
            lockedTableOnRestaurant.put(slotId, lockedTable);
            lockedTableOnDate.put(restaurantId, lockedTableOnRestaurant);
            lockedTableDetails.put(date, lockedTableOnDate);
        }
    }

    @Override
    public boolean isLocked(String date, String restaurantId, String slotId, String tableId) {
        Map<String, Map<String, List<String>>> lockedTableOnDate = lockedTableDetails.getOrDefault(date, new HashMap<>());
        Map<String, List<String>> lockedTableOnRestaurant = lockedTableOnDate.getOrDefault(restaurantId, new HashMap<>());
        List<String> lockedTable = lockedTableOnRestaurant.getOrDefault(slotId, new ArrayList<>());
        if (lockedTable.contains(tableId))
            return true;
        else
            return false;
    }

    @Override
    public void releaseLock(String date, String restaurantId, String slotId, String tableId) {
        Map<String, Map<String, List<String>>> lockedTableOnDate = lockedTableDetails.getOrDefault(date, new HashMap<>());
        Map<String, List<String>> lockedTableOnRestaurant = lockedTableOnDate.getOrDefault(restaurantId, new HashMap<>());
        List<String> lockedTable = lockedTableOnRestaurant.getOrDefault(slotId, new ArrayList<>());
        lockedTable.remove(tableId);
    }

}
