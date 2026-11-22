package com.example.lab4.infrastructure.bootstrap;

import com.example.lab4.application.commands.customers.SaveCustomerCommand;
import com.example.lab4.application.commands.customers.SaveCustomersCommand;
import com.example.lab4.application.commands.deliveries.SaveDeliveriesCommand;
import com.example.lab4.application.commands.deliveries.SaveDeliveryCommand;
import com.example.lab4.application.commands.depots.SaveDepotCommand;
import com.example.lab4.application.commands.depots.SaveDepotsCommand;
import com.example.lab4.application.commands.drivers.SaveDriverCommand;
import com.example.lab4.application.commands.drivers.SaveDriversCommand;
import com.example.lab4.application.commands.vehicles.SaveVehicleCommand;
import com.example.lab4.application.commands.vehicles.SaveVehiclesCommand;
import com.example.lab4.application.cqrs.CommandBus;
import com.example.lab4.application.cqrs.QueryBus;
import com.example.lab4.application.queries.customers.FindCustomerByEmailQuery;
import com.example.lab4.application.queries.customers.GetCustomersQuery;
import com.example.lab4.application.queries.deliveries.DeliveryExistsForCustomerWithNotesQuery;
import com.example.lab4.application.queries.depots.FindDepotByNameQuery;
import com.example.lab4.application.queries.drivers.FindDriverByEmailQuery;
import com.example.lab4.application.queries.vehicles.FindVehicleByNameQuery;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import com.example.lab4.domain.entities.User;
import com.example.lab4.domain.entities.Delivery;
import com.example.lab4.domain.entities.Depot;
import com.example.lab4.domain.entities.Vehicle;

import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Value;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoDataInitializer {
    @Bean
    CommandLineRunner bootstrapData(QueryBus queries, CommandBus commands,
                                       @Value("${app.seed.enabled:true}") boolean seedEnabled,
                                       @Value("${app.seed.depots:10}") int depotCount,
                                       @Value("${app.seed.vehicles:50}") int vehicleCount,
                                       @Value("${app.seed.customers:1000}") int customerCount,
                                       @Value("${app.seed.deliveries-per-customer:3}") int deliveriesPerCustomer) {
        return args -> {
            Depot depot = queries.execute(new FindDepotByNameQuery("Demo Depot"))
                    .orElseGet(() -> commands.execute(new SaveDepotCommand(new Depot("Demo Depot", "44.4268,26.1025"))));
            User driver = queries.execute(new FindDriverByEmailQuery("driver@example.com"))
                    .orElseGet(() -> commands.execute(new SaveDriverCommand(User.driver("Demo Driver", "driver@example.com", "+40987654321"))));
            Vehicle vehicle = queries.execute(new FindVehicleByNameQuery("Demo Vehicle"))
                    .orElseGet(() -> commands.execute(new SaveVehicleCommand(new Vehicle("Demo Vehicle", 100, depot, true, driver))));
            User customer = queries.execute(new FindCustomerByEmailQuery("demo@example.com"))
                    .orElseGet(() -> commands.execute(new SaveCustomerCommand(User.customer("Demo Customer", "demo@example.com",
                            "+40123456789", "1 Example Street", "44.4320,26.1030"))));

            String demoDeliveryNote = "Created by the lab4 service layer demo";
            if (!queries.execute(new DeliveryExistsForCustomerWithNotesQuery(customer.getId(), demoDeliveryNote))) {
                commands.execute(new SaveDeliveryCommand(new Delivery(customer, 5, demoDeliveryNote, OffsetDateTime.now())));
            }

            System.out.printf("Bootstrap data: depot=%d, driver=%d, vehicle=%d, customer=%d%n",
                    depot.getId(), driver.getId(), vehicle.getId(), customer.getId());

            if (seedEnabled) {
                seedDatabase(commands, queries,
                        depotCount, vehicleCount, customerCount, deliveriesPerCustomer);
            }
        };
    }

    private void seedDatabase(CommandBus commands, QueryBus queries, int depotCount, int vehicleCount,
                              int customerCount, int deliveriesPerCustomer) {
        if (depotCount < 1 || vehicleCount < 1 || customerCount < 1 || deliveriesPerCustomer < 1) {
            throw new IllegalArgumentException("Seed counts must all be positive.");
        }

        Faker faker = new Faker();
        if (queries.execute(new FindCustomerByEmailQuery("seed.customer.0@vros.example")).isPresent()) {
            System.out.println("Infrastructure seed already exists; checking for missing generated customers.");
            seedCustomers(commands, queries, faker, customerCount, deliveriesPerCustomer);
            return;
        }

        List<Depot> depots = new ArrayList<>();
        for (int i = 0; i < depotCount; i++) {
            depots.add(new Depot(faker.address().city() + " Depot " + i, randomCoordinates(faker)));
        }
        depots = commands.execute(new SaveDepotsCommand(depots));

        List<User> drivers = new ArrayList<>();
        for (int i = 0; i < vehicleCount; i++) {
            drivers.add(User.driver(faker.name().fullName(), "seed.driver." + i + "@vros.example",
                    "+40" + String.format("%09d", 600_000_000 + i)));
        }
        drivers = commands.execute(new SaveDriversCommand(drivers));

        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < vehicleCount; i++) {
            vehicles.add(new Vehicle(faker.company().name() + " Vehicle " + i,
                    faker.number().numberBetween(50, 2_001), depots.get(i % depots.size()),
                    true, drivers.get(i)));
        }
        commands.execute(new SaveVehiclesCommand(vehicles));

        System.out.printf("DataFaker populated %d depots, %d drivers, and %d vehicles.%n",
                depots.size(), drivers.size(), vehicleCount);
        seedCustomers(commands, queries, faker, customerCount, deliveriesPerCustomer);
    }

    private void seedCustomers(CommandBus commands, QueryBus queries,
                               Faker faker, int customerCount, int deliveriesPerCustomer) {
        Set<String> existingEmails = queries.execute(new GetCustomersQuery()).stream()
                .map(User::getEmail).collect(Collectors.toSet());
        List<User> customers = new ArrayList<>();
        for (int i = 0; i < customerCount; i++) {
            String email = "seed.customer." + i + "@vros.example";
            if (existingEmails.contains(email)) {
                continue;
            }
            customers.add(User.customer(faker.name().fullName(), email,
                    faker.phoneNumber().toString(), faker.address().streetAddress(),
                    randomCoordinates(faker)));
        }
        customers = commands.execute(new SaveCustomersCommand(customers));

        List<Delivery> generatedDeliveries = new ArrayList<>();
        for (int i = 0; i < customers.size(); i++) {
            for (int j = 0; j < deliveriesPerCustomer; j++) {
                generatedDeliveries.add(new Delivery(customers.get(i),
                        faker.number().numberBetween(1, 51), faker.lorem().sentence(),
                        OffsetDateTime.now().minusDays(faker.number().numberBetween(0, 366))));
            }
        }
        commands.execute(new SaveDeliveriesCommand(generatedDeliveries));
        System.out.printf("DataFaker added %d customers and %d deliveries (generated customer target: %d).%n",
                customers.size(), generatedDeliveries.size(), customerCount);
    }

    private String randomCoordinates(Faker faker) {
        return "44." + String.format("%06d", faker.number().numberBetween(0, 1_000_000))
                + ",26." + String.format("%06d", faker.number().numberBetween(0, 1_000_000));
    }

}
