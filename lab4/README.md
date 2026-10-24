# Lab 4 – Java Persistence API

Spring Boot + Spring Data JPA project backed by PostgreSQL. The schema is created by `src/main/resources/schema.sql`; Hibernate validates the entity mappings against it on startup.

## Run

```bash
docker compose up -d
./mvnw spring-boot:run
```

The `db` service exposes PostgreSQL on `localhost:5432` with database/user/password `lab4`. The application uses those settings by default and can be pointed elsewhere with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`.

On startup, a `CommandLineRunner` exercises all five services by finding or creating a demo depot, driver, vehicle, customer, and delivery, then prints their IDs. The sample data is reused across application restarts.

## Repository queries

- **Derived query:** `CustomerRepository.findByEmail` looks up a customer by its unique email.
- **JPQL query:** `VehicleRepository.findAvailableAtDepot` lists available vehicles assigned to a depot.
- **Transactional modifying query:** `VehicleRepository.updateAvailability` updates a vehicle's availability by ID and returns the number of affected rows. It runs inside a transaction and clears/flushes the persistence context around the bulk update.

To stop PostgreSQL while retaining its data, run `docker compose down`. To also delete the database volume, run `docker compose down -v`.
