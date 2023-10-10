package com.rbs.restaurantbookingsystem;

import com.rbs.restaurantbookingsystem.enums.CuisineType;
import com.rbs.restaurantbookingsystem.enums.RestaurantType;
import com.rbs.restaurantbookingsystem.enums.SlotStatus;
import com.rbs.restaurantbookingsystem.enums.TableStatus;
import com.rbs.restaurantbookingsystem.logging.ConsoleLogger;
import com.rbs.restaurantbookingsystem.logging.Logger;
import com.rbs.restaurantbookingsystem.managers.BookingManager;
import com.rbs.restaurantbookingsystem.managers.CuisineManager;
import com.rbs.restaurantbookingsystem.managers.DefaultTableLockManger;
import com.rbs.restaurantbookingsystem.managers.LockManager;
import com.rbs.restaurantbookingsystem.managers.RestaurantManager;
import com.rbs.restaurantbookingsystem.models.Restaurant;
import com.rbs.restaurantbookingsystem.models.RestaurantSlot;
import com.rbs.restaurantbookingsystem.models.Table;
import com.rbs.restaurantbookingsystem.models.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RestaurantBookingSystemApplicationTests {
    private CuisineManager cuisineManager;
    private RestaurantManager restaurantManager;

    private LockManager lockManager;

    private Logger logger;

    private BookingManager bookingManager;

    @BeforeAll
    public void init() {
        this.cuisineManager = CuisineManager.getIns();
        this.logger = new ConsoleLogger();
        this.lockManager = new DefaultTableLockManger();
        this.restaurantManager = RestaurantManager.getIns(cuisineManager, logger, lockManager);
        this.bookingManager = BookingManager.getIns(restaurantManager, lockManager, logger);
    }

    @Test
    public void runner() {
        Restaurant r1 = restaurantManager.registerRestaurant("ABC", "Bang", "HSR", RestaurantType.BOTH, new BigDecimal(300));
        restaurantManager.markActiveRestaurant(r1.getId());
        logger.log(restaurantManager.searchByArea("HSR"));

        Restaurant r2 = restaurantManager.registerRestaurant("XYZ", "Bangalore", "BTM", RestaurantType.BOTH, new BigDecimal(300));
        restaurantManager.markActiveRestaurant(r2.getId());
        Restaurant r3 = restaurantManager.registerRestaurant("NKM", "Bangalore", "BTM", RestaurantType.BOTH, new BigDecimal(300));
        restaurantManager.markActiveRestaurant(r3.getId());
        Restaurant r4 = restaurantManager.registerRestaurant("ABC", "Bangalore", "BTM", RestaurantType.BOTH, new BigDecimal(300));
        restaurantManager.markActiveRestaurant(r4.getId());
        //   Restaurant r5 = restaurantManager.registerRestaurant("ABC", "Bang", "HSR", RestaurantType.BOTH, new BigDecimal(300));
        logger.log(restaurantManager.searchByCity("Delhi"));
        logger.log(restaurantManager.searchByCity("Bangalore"));

        // Add cuisineType to the Restaurants
        restaurantManager.addCuisine(r1.getId(), List.of(CuisineType.INDIAN, CuisineType.CHINESE));
        restaurantManager.addCuisine(r2.getId(), List.of(CuisineType.INDIAN));

        logger.log("Cuisine Search :::: " + restaurantManager.searchByCuisine(CuisineType.INDIAN));

        Table table1 = Table.builder()
                .tableNumber("1A")
                .tableStatus(TableStatus.AVAILABLE)
                .capacity(6)
                .build();

        Table table2 = Table.builder()
                .tableNumber("1B")
                .tableStatus(TableStatus.AVAILABLE)
                .capacity(4)
                .build();

        Table table3 = Table.builder()
                .tableNumber("1C")
                .tableStatus(TableStatus.AVAILABLE)
                .capacity(8)
                .build();

        RestaurantSlot restaurantSlot = new RestaurantSlot();
        restaurantSlot.addTable(table1);
        restaurantSlot.addTable(table2);
        restaurantSlot.addTable(table3);
        restaurantSlot.setFrom(10);
        restaurantSlot.setTo(11);
        restaurantSlot.setSlotStatus(SlotStatus.AVAILABLE);

        restaurantManager.addSlot(r1.getId(), "12-08-2023", List.of(restaurantSlot));

        restaurantManager.getAvailableSlotForRestaurant(r1.getId(), "12-08-2023");

        User user = new User("Mukul", 9090909090L);
        bookingManager.bookSlotAndTable(user, "12-08-2023", r1.getId(), restaurantSlot.getId(), table3.getId());

        User user1 = new User("Jack", 9090909080L);
        bookingManager.bookSlotAndTable(user1, "12-08-2023", r1.getId(), restaurantSlot.getId(), table2.getId());
    }
}
