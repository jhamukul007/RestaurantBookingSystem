package com.rbs.restaurantbookingsystem.managers;

public interface LockManager {
    void lock(String date, String restaurantId, String slotId, String tableId);

    boolean isLocked(String date, String restaurantId, String slotId, String tableId);

    void releaseLock(String date, String restaurantId, String slotId, String tableId);
}
