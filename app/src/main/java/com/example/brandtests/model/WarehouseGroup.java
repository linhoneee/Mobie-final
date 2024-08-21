package com.example.brandtests.model;

import java.util.ArrayList;
import java.util.List;

public class WarehouseGroup {
    private Long warehouseId;
    private List<Item> items;

    public WarehouseGroup(Long warehouseId) {
        this.warehouseId = warehouseId;
        this.items = new ArrayList<>();
    }

    public Long getWarehouseId() {
        return warehouseId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        this.items.add(item);
    }
}
