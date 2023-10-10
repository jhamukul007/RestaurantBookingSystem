package com.rbs.restaurantbookingsystem.models;

import com.rbs.restaurantbookingsystem.enums.TableStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Table extends BaseEntity{
    private String tableNumber;
    // no of user can seat
    private Integer capacity;
    private TableStatus tableStatus;
}
