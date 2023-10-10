package com.rbs.restaurantbookingsystem.managers;

import com.rbs.restaurantbookingsystem.enums.SlotStatus;
import com.rbs.restaurantbookingsystem.enums.TableStatus;
import com.rbs.restaurantbookingsystem.exceptions.SlotNotAvailableException;
import com.rbs.restaurantbookingsystem.exceptions.SlotNotFoundException;
import com.rbs.restaurantbookingsystem.exceptions.TableLockException;
import com.rbs.restaurantbookingsystem.exceptions.TableNotAvailableException;
import com.rbs.restaurantbookingsystem.logging.Logger;
import com.rbs.restaurantbookingsystem.models.Restaurant;
import com.rbs.restaurantbookingsystem.models.RestaurantSlot;
import com.rbs.restaurantbookingsystem.models.Table;
import com.rbs.restaurantbookingsystem.models.TableBooking;
import com.rbs.restaurantbookingsystem.models.User;
import lombok.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

public class BookingManager {
    private final RestaurantManager restaurantManager;
    private final LockManager lockManager;

    private static BookingManager ins;

    private final Logger logger;

    private BookingManager(RestaurantManager restaurantManager, LockManager lockManager, Logger logger) {
        this.restaurantManager = restaurantManager;
        this.lockManager = lockManager;
        this.logger = logger;
    }

    public static BookingManager getIns(RestaurantManager restaurantManager, LockManager lockManager, Logger logger) {
        if (ins == null) {
            synchronized (RestaurantManager.class) {
                if (ins == null)
                    ins = new BookingManager(restaurantManager, lockManager, logger);
            }
        }
        return ins;
    }

    public void bookSlotAndTable(@NonNull User user, @NonNull String date, @NonNull String restaurantId, @NonNull String slotId, @NonNull String tableId) {
        Restaurant restaurant = restaurantManager.getOrThrowNotFound(restaurantId);
        TreeSet<RestaurantSlot> slots = restaurant.getDateSlotMap().get(date);
        RestaurantSlot slot = slots.stream().filter(restaurantSlot -> Objects.deepEquals(restaurantSlot.getId(), slotId)).findFirst().orElseThrow(() -> new SlotNotFoundException());
        if (!SlotStatus.AVAILABLE.equals(slot.getSlotStatus()))
            throw new SlotNotAvailableException();
        List<Table> availableTables = slot.getAvailableTable();
        Table table = availableTables.stream().filter(table1 -> Objects.deepEquals(tableId, table1.getId())).findFirst().orElseThrow(() -> new TableNotAvailableException());
        if (!TableStatus.AVAILABLE.equals(table.getTableStatus()))
            throw new TableNotAvailableException();
        if (lockManager.isLocked(date, restaurantId, slotId, tableId))
            throw new TableLockException();
        lockManager.isLocked(date, restaurantId, slotId, tableId);
        table.setTableStatus(TableStatus.BOOKED);
        TableBooking tableBooking = TableBooking.builder()
                .table(table)
                .date(date)
                .restaurantSlot(slot)
                .build();
        user.addBooking(tableBooking);
        lockManager.releaseLock(date, restaurantId, slotId, tableId);
        logger.log(String.format("User %s has been booked table %s at %s on date %s in restaurant %s", user.getName(), table.getTableNumber(), slot.getFrom(), date, restaurant.getName()));
    }

}
