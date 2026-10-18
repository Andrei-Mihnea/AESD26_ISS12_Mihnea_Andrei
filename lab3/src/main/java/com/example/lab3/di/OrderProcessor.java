package com.example.lab3.di;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessor {
    private final InventoryService constructorInventory;

    @Autowired
    private InventoryService fieldInventory;

    private InventoryService setterInventory;
    private InventoryService methodInventory;

    public OrderProcessor(InventoryService constructorInventory) {
        this.constructorInventory = constructorInventory;
        System.out.println("1. Constructor injection");
    }

    @Autowired
    void configureMethodInventory(InventoryService methodInventory)
    {
        this.methodInventory = methodInventory;
        System.out.println("4. Method injection");
    }

    @Autowired
    public void setInventory(InventoryService inventoryService) {
        System.out.println("Field already injected: " + (fieldInventory != null));
        this.setterInventory = inventoryService;
        System.out.println("3. Setter injection");
    }

    public void afterConstructionAndFieldInjection() {
        System.out.println("2. Field injection completed");
    }

    public String process(String item) {
        return constructorInventory.checkStock(item)
                + " | all injection points received the service";
    }
}
