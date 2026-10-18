package com.example.lab3.di;

import org.springframework.stereotype.Component;

@Component
public class InventoryService {
    public String checkStock(String item)
    {
        return item + " is in stock";
    }
}