# Lab 4 – Java Persistence API

Spring Boot + Spring Data JPA project backed by PostgreSQL. The schema is created by `src/main/resources/schema.sql`; Hibernate validates the entity mappings against it on startup.

## Run

```bash
docker compose up -d
./mvnw spring-boot:run
```

The `db` service exposes PostgreSQL on `localhost:5432` with database/user/password `lab4`. The application uses those settings by default and can be pointed elsewhere with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`.

On startup, a `CommandLineRunner` exercises all five services by finding or creating a demo depot, driver, vehicle, customer, and delivery, then prints their IDs. DataFaker also creates 10 depots, 50 drivers and vehicles, 500 customers, and 1,500 deliveries on the first run. A seed marker prevents adding the same batch on each restart. Configure the counts with `APP_SEED_DEPOTS`, `APP_SEED_VEHICLES`, `APP_SEED_CUSTOMERS`, and `APP_SEED_DELIVERIES_PER_CUSTOMER`; set `APP_SEED_ENABLED=false` to disable generated data.

## Repository queries

- **Derived query:** `CustomerRepository.findByEmail` looks up a customer by its unique email.
- **JPQL query:** `VehicleRepository.findAvailableAtDepot` lists available vehicles assigned to a depot.
- **Transactional modifying query:** `VehicleRepository.updateAvailability` updates a vehicle's availability by ID and returns the number of affected rows. It runs inside a transaction and clears/flushes the persistence context around the bulk update.
- **Criteria API search:** `DeliveryService.search(DeliverySearchCriteria)` supports any combination of customer ID, area text matched against customer address or coordinates, quantity greater than a threshold, minimum/maximum quantity, case-insensitive notes text, timestamp bounds, and a specific registration date. Date-only searches use UTC day boundaries. Null or blank text filters are ignored, and results are ordered newest first.

Example: `deliveryService.search(new DeliverySearchCriteria(customerId, "Bucharest", 10, null, null, null, null, null, registeredOn))` returns deliveries for that customer, in an area matching “Bucharest”, with quantity greater than 10 and registered on that UTC date. Pass `null` for any filter you want to omit.

## Customer lookup cache and benchmark

`CustomerService.findByEmail` is cached in Caffeine (`customersByEmail`) with a 10-minute write expiry and a maximum of 10,000 entries. Customer saves and deletes evict the cache so later lookups cannot return stale records.

To measure cached service reads against uncached repository reads, start PostgreSQL and run:

```bash
docker compose up -d
./mvnw -Pjmh -DskipTests package
mkdir -p benchmark-results
java -cp 'target/classes:target/dependency/*' org.openjdk.jmh.Main CustomerReadBenchmark -rf json -rff benchmark-results/customer-read-jmh.json
```

The JMH benchmark uses 5 one-second warm-up iterations, 10 one-second measurement iterations, and 2 independent JVM forks. Spring starts and the cache is warmed outside the timed benchmark methods. Results are reported as average microseconds per lookup; compare `cachedServiceRead` with `uncachedRepositoryRead` on the same machine and database.

Measured on 2026-10-24 with JMH 1.37, OpenJDK 21.0.12.1, macOS ARM64, and the local Docker PostgreSQL database: the cached service lookup averaged **0.279 ± 0.003 µs/op**, while the uncached repository lookup averaged **107.383 ± 2.047 µs/op**. That is about **384× lower read latency** for this warmed, single-key cache-hit workload. The raw JMH output is in `benchmark-results/customer-read-jmh.json`; results depend on the host and database and should be treated as a local measurement rather than a general performance guarantee.

To stop PostgreSQL while retaining its data, run `docker compose down`. To also delete the database volume, run `docker compose down -v`.
