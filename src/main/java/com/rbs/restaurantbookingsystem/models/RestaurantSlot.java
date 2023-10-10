package com.rbs.restaurantbookingsystem.models;

import com.rbs.restaurantbookingsystem.enums.SlotStatus;
import com.rbs.restaurantbookingsystem.enums.TableStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RestaurantSlot extends BaseEntity {
    //private LocalDateTime localDateTime;
    // 24 hr time format
    @EqualsAndHashCode.Include
    private Integer from;

    @EqualsAndHashCode.Include
    private Integer to;

    private SlotStatus slotStatus;

    private List<Table> tableList;

    public RestaurantSlot() {
        this.tableList = new ArrayList<>();
    }

    public List<Table> getAvailableTable(){
        return tableList.stream().filter(table -> TableStatus.AVAILABLE.equals(table.getTableStatus())).collect(Collectors.toList());
    }

    public void addTable(Table table){
        this.tableList.add(table);
    }

}
