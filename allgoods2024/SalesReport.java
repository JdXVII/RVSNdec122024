package com.example.allgoods2024;

import java.util.Map;

public class SalesReport {
    private String startDate;
    private String endDate;
    private double totalSales;
    private int totalQuantity;
    private Map<String, CategorySales> categorySalesMap;

    public SalesReport() {

    }

    public SalesReport(String startDate, String endDate, double totalSales, int totalQuantity, Map<String, CategorySales> categorySalesMap) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalSales = totalSales;
        this.totalQuantity = totalQuantity;
        this.categorySalesMap = categorySalesMap;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(double totalSales) {
        this.totalSales = totalSales;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Map<String, CategorySales> getCategorySalesMap() {
        return categorySalesMap;
    }

    public void setCategorySalesMap(Map<String, CategorySales> categorySalesMap) {
        this.categorySalesMap = categorySalesMap;
    }
}


