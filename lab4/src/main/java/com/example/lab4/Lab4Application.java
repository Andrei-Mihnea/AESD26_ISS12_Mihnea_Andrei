package com.example.lab4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.cache.annotation.EnableCaching;
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
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Value;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@EnableCaching
public class Lab4Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab4Application.class, args);
    }

    @Bean
    CommandLineRunner serviceLayerDemo(DepotService depots, DriverService drivers,
                                       VehicleService vehicles, CustomerService customers,
                                       DeliveryService deliveries,
                                       @Value("${app.seed.enabled:true}") boolean seedEnabled,
                                       @Value("${app.seed.depots:10}") int depotCount,
                                       @Value("${app.seed.vehicles:50}") int vehicleCount,
                                       @Value("${app.seed.customers:500}") int customerCount,
                                       @Value("${app.seed.deliveries-per-customer:3}") int deliveriesPerCustomer) {
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

            if (seedEnabled) {
                seedDatabase(depots, drivers, vehicles, customers, deliveries,
                        depotCount, vehicleCount, customerCount, deliveriesPerCustomer);
            }
        };
    }

    private void seedDatabase(DepotService depotService, DriverService driverService,
                              VehicleService vehicleService, CustomerService customerService,
                              DeliveryService deliveryService, int depotCount, int vehicleCount,
                              int customerCount, int deliveriesPerCustomer) {
        String firstSeedEmail = "seed.customer.0@vros.example";
        if (customerService.findByEmail(firstSeedEmail).isPresent()) {
            System.out.println("DataFaker seed already exists; skipping database population.");
            return;
        }
        if (depotCount < 1 || vehicleCount < 1 || customerCount < 1 || deliveriesPerCustomer < 1) {
            throw new IllegalArgumentException("Seed counts must all be positive.");
        }

        Faker faker = new Faker();
        List<Depot> depots = new ArrayList<>();
        for (int i = 0; i < depotCount; i++) {
            depots.add(new Depot(faker.address().city() + " Depot " + i, randomCoordinates(faker)));
        }
        depots = depotService.saveAll(depots);

        List<Driver> drivers = new ArrayList<>();
        for (int i = 0; i < vehicleCount; i++) {
            drivers.add(new Driver(faker.name().fullName(), "seed.driver." + i + "@vros.example",
                    "+40" + String.format("%09d", 600_000_000 + i)));
        }
        drivers = driverService.saveAll(drivers);

        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < vehicleCount; i++) {
            vehicles.add(new Vehicle(faker.company().name() + " Vehicle " + i,
                    faker.number().numberBetween(50, 2_001), depots.get(i % depots.size()),
                    true, drivers.get(i)));
        }
        vehicleService.saveAll(vehicles);

        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < customerCount; i++) {
            customers.add(new Customer(faker.name().fullName(), "seed.customer." + i + "@vros.example",
                    "+40" + String.format("%09d", 700_000_000 + i), faker.address().streetAddress(),
                    randomCoordinates(faker)));
        }
        customers = customerService.saveAll(customers);

        List<Delivery> generatedDeliveries = new ArrayList<>();
        for (int i = 0; i < customers.size(); i++) {
            for (int j = 0; j < deliveriesPerCustomer; j++) {
                generatedDeliveries.add(new Delivery(customers.get(i),
                        faker.number().numberBetween(1, 51), faker.lorem().sentence(),
                        OffsetDateTime.now().minusDays(faker.number().numberBetween(0, 366))));
            }
        }
        deliveryService.saveAll(generatedDeliveries);
        System.out.printf("DataFaker populated %d depots, %d drivers, %d vehicles, %d customers, and %d deliveries.%n",
                depots.size(), drivers.size(), vehicleCount, customers.size(), generatedDeliveries.size());
    }

    private String randomCoordinates(Faker faker) {
        return "44." + String.format("%06d", faker.number().numberBetween(0, 1_000_000))
                + ",26." + String.format("%06d", faker.number().numberBetween(0, 1_000_000));
    }

}
