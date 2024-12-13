package com.example.allgoods2024;

import java.util.Map;

public class CategorySales {
    private Map<String, ItemSales> itemSalesMap; // Key: productId

    public CategorySales() {

    }

    public CategorySales(Map<String, ItemSales> itemSalesMap) {
        this.itemSalesMap = itemSalesMap;
    }

    public Map<String, ItemSales> getItemSalesMap() {
        return itemSalesMap;
    }

    public void setItemSalesMap(Map<String, ItemSales> itemSalesMap) {
        this.itemSalesMap = itemSalesMap;
    }
}


