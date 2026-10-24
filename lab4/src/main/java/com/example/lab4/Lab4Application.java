package com.example.lab4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.example.lab4.entity.Customer;
import com.example.lab4.entity.Delivery;
import com.example.lab4.entity.Depot;
import com.example.lab4.entity.Driver;
import com.example.lab4.entity.Vehicle;
import com.example.lab4.service.CustomerService;
import com.example.lab4.service.DeliveryService;
import com.example.lab4.service.DepotService;
import com.example.lab4.service.DriverService;
import com.example.lab4.service.VehicleService;

import java.time.OffsetDateTime;

@SpringBootApplication
public class Lab4Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab4Application.class, args);
    }

    @Bean
    CommandLineRunner serviceLayerDemo(DepotService depots, DriverService drivers,
                                       VehicleService vehicles, CustomerService customers,
                                       DeliveryService deliveries) {
        return args -> {
            Depot depot = depots.findByName("Demo Depot")
                    .orElseGet(() -> depots.save(new Depot("Demo Depot", "44.4268,26.1025")));
            Driver driver = drivers.findByEmail("driver@example.com")
                    .orElseGet(() -> drivers.save(new Driver("Demo Driver", "driver@example.com", "+40987654321")));
            Vehicle vehicle = vehicles.findByName("Demo Vehicle")
                    .orElseGet(() -> vehicles.save(new Vehicle("Demo Vehicle", 100, depot, true, driver)));
            Customer customer = customers.findByEmail("demo@example.com")
                    .orElseGet(() -> customers.save(new Customer("Demo Customer", "demo@example.com",
                            "+40123456789", "1 Example Street", "44.4320,26.1030")));

            String demoDeliveryNote = "Created by the lab4 service layer demo";
            if (!deliveries.existsForCustomerWithNotes(customer.getId(), demoDeliveryNote)) {
                deliveries.save(new Delivery(customer, 5, demoDeliveryNote, OffsetDateTime.now()));
            }

            System.out.printf("Service layer check: depot=%d, driver=%d, vehicle=%d, customer=%d%n",
                    depot.getId(), driver.getId(), vehicle.getId(), customer.getId());
        };
    }

}
