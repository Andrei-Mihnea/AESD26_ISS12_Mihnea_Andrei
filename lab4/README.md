# Lab 4 – Java Persistence API

Spring Boot + Spring Data JPA project backed by PostgreSQL. Flyway applies versioned SQL migrations from `src/main/resources/db/migration` before Hibernate validates the entity mappings on startup.

The preferred API prefix is `/api/v2`. Explicit `/api/v1` routes and the original unversioned paths remain supported compatibility routes. Older examples below still work; see the version migration strategy at the end.

## Run

```bash
docker compose up -d
./mvnw spring-boot:run
```

The `db` service exposes PostgreSQL on `localhost:5432` with database/user/password `lab4`. The application uses those settings by default and can be pointed elsewhere with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`.

On startup, a `CommandLineRunner` exercises all five services by finding or creating a demo depot, driver, vehicle, customer, and delivery, then prints their IDs. DataFaker also creates 10 depots, 50 drivers and vehicles, 1,000 customers, and 3,000 deliveries on the first run. Existing generated customer emails are skipped on restart. Increasing `APP_SEED_CUSTOMERS` adds the missing generated customers and their deliveries without duplicating the existing batch. The demo customer and manually created customers are additional to this generated customer target; lowering the target does not delete data. Configure the counts with `APP_SEED_DEPOTS`, `APP_SEED_VEHICLES`, `APP_SEED_CUSTOMERS`, and `APP_SEED_DELIVERIES_PER_CUSTOMER`; set `APP_SEED_ENABLED=false` to disable generated data.

## Routing scenarios

`Scenario` has a name and two sets: selected vehicles and selected deliveries. Both are unidirectional `@ManyToMany` associations owned by `Scenario`, stored in `scenario_vehicles` and `scenario_deliveries`. A vehicle or delivery may be reused in multiple scenarios. Composite primary keys prevent duplicate memberships. Flyway migration `V2__add_routing_scenarios.sql` adds these tables without replacing existing data.

Persist vehicles and deliveries first, then select them for a scenario inside a transaction:

```java
Scenario scenario = new Scenario("Morning routing problem");
scenario.addVehicle(vehicle);
scenario.addDelivery(delivery);
scenarioService.save(scenario);
```

`ScenarioRepository` provides JPA persistence and `ScenarioService` provides find, save and delete operations. Use `addVehicle`, `removeVehicle`, `addDelivery`, and `removeDelivery` to edit membership. Access the lazy sets inside a transaction. Deleting a scenario removes its join-table rows while preserving shared vehicles and deliveries; no JPA persist/remove cascades are configured for members. Membership does not assign a delivery to a particular vehicle or calculate routes.

With PostgreSQL running, `./mvnw -Dapp.seed.enabled=false test` runs `RoutingApiTests` against an HTTP server on a random port. It verifies CRUD, validation, missing references, failed scenario updates, shared membership, and safe deletion. Test resources are cleaned up after execution.

## Customer REST API (compulsory)

The application serves HTTP on port 8080. Spring Web MVC exposes customer CRUD, Bean Validation checks incoming details, and `springdoc-openapi-starter-webmvc-ui` 3.2.0 generates interactive API documentation compatible with Spring Boot 4.1.

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

| Method | Endpoint | Success |
| --- | --- | --- |
| POST | `/api/customers` | `201 Created`, customer JSON and a `Location` header |
| GET | `/api/customers` | `200 OK`, customer array |
| GET | `/api/customers/{id}` | `200 OK`, customer JSON |
| PUT | `/api/customers/{id}` | `200 OK`, updated customer JSON |
| DELETE | `/api/customers/{id}` | `204 No Content` |

POST and PUT require `name`, `email`, `phone`, `address`, and `coordinates`. Every field must be nonblank; email must be valid. Maximum lengths match the database: name 120, phone 40, and the other fields 255 characters. PUT replaces all five details of an existing customer. Responses include the generated `id` and these details, without serializing the delivery relationship.

Invalid bodies or nonnumeric IDs return `400 Bad Request`. Missing customers return `404 Not Found`. Duplicate emails and attempts to delete customers with deliveries return `409 Conflict`; deliveries retain the existing database restriction. Successful creates, updates and deletes evict the customer email cache.

### Test with Swagger UI

With PostgreSQL and the application running, open [Swagger UI](http://localhost:8080/swagger-ui.html) and expand **Customers**. For each operation, click **Try it out**, fill in the parameters or request body, then click **Execute** to inspect the status, headers, and response body.

1. Execute **POST /api/customers** with the JSON below. Expect `201 Created`; copy the returned `id` and check the `Location` header.
2. Execute **GET /api/customers** and **GET /api/customers/{id}** using that ID. Expect `200 OK` and the created customer's details.
3. Execute **PUT /api/customers/{id}** with the same ID and all five fields, changing the name or address. Expect `200 OK`. Retrieve the customer again to confirm the update.
4. Execute **DELETE /api/customers/{id}**. Expect `204 No Content`. Retrieve the deleted ID again and expect `404 Not Found`.

Example POST/PUT request body:

```json
{
  "name": "Ana Popescu",
  "email": "ana.swagger@example.com",
  "phone": "+40712345678",
  "address": "1 Example Street",
  "coordinates": "44.4268,26.1025"
}
```

Also check error responses through **Try it out**: submit a blank name or invalid email for `400`, reuse an existing email for `409`, and use a deleted customer ID for `404`. Attempting to delete the startup demo customer (`demo@example.com`), which has a delivery, returns `409`.

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


## Vehicle, delivery, and scenario REST APIs

All three resources support the following operations and are documented in [Swagger UI](http://localhost:8080/swagger-ui.html):

| Operation | Vehicles | Deliveries | Scenarios | Success |
| --- | --- | --- | --- | --- |
| Create | `POST /api/vehicles` | `POST /api/deliveries` | `POST /api/scenarios` | 201 + Location header |
| List | `GET /api/vehicles` | `GET /api/deliveries` | `GET /api/scenarios` | 200 |
| Read | `GET /api/vehicles/{id}` | `GET /api/deliveries/{id}` | `GET /api/scenarios/{id}` | 200 |
| Replace | `PUT /api/vehicles/{id}` | `PUT /api/deliveries/{id}` | `PUT /api/scenarios/{id}` | 200 |
| Delete | `DELETE /api/vehicles/{id}` | `DELETE /api/deliveries/{id}` | `DELETE /api/scenarios/{id}` | 204 |

Vehicle POST/PUT body:

```json
{"name":"Van A","capacity":100,"available":true}
```

Capacity must be positive and the name must be nonblank, unique, and at most 120 characters. The API selects the default depot automatically and leaves the driver unassigned; no depot or driver fields are needed in requests. To support existing lab4 data with multiple depots, the depot with the lowest ID is used consistently. Existing seed records are preserved. If there is no depot, vehicle writes return 409.

Delivery POST/PUT body (replace `customerId` with an existing customer ID):

```json
{"customerId":1,"quantity":10,"notes":"Fragile","timestamp":"2026-10-31T12:00:00Z"}
```

Customer ID, positive quantity, and an ISO 8601 timestamp with an offset are required. Notes are optional. PUT can change the customer as well as the other delivery details.

Scenario POST/PUT body (replace IDs with existing resources):

```json
{"name":"Morning routes","vehicleIds":[1,2],"deliveryIds":[1,2,3]}
```

The name must be nonblank and at most 120 characters. Both ID sets are required; empty sets are allowed. PUT replaces the name and both sets, so empty arrays clear the memberships. Missing referenced customers, vehicles, or deliveries return 404; invalid bodies return 400. Failed updates leave the original scenario intact. Resource names other than vehicle names need not be unique.

Deleting a scenario preserves its selected resources. Deleting a vehicle or delivery removes its memberships from all scenarios while preserving the scenarios. Responses use IDs for relationships and are built inside service transactions, with `spring.jpa.open-in-view=false`.

To test in Swagger UI, create a vehicle, then a delivery for an existing customer, then a scenario containing their IDs. Use GET to inspect each resource, PUT to change it, and DELETE to remove it. Verify 404 after deletion and 400 for invalid bodies. Reusing a vehicle name returns 409. Deleting a customer who still has deliveries continues to return 409. Scenario solving belongs to a future phase.


## Custom runtime exception

`ResourceNotFoundException` in `com.example.lab4.exception` extends `RuntimeException`. Customer endpoints and the vehicle, delivery, and scenario services throw it when a requested resource or referenced entity is missing. `ApiExceptionHandler`, registered globally through `@RestControllerAdvice`, handles it with `@ExceptionHandler(ResourceNotFoundException.class)` and returns HTTP 404 with a `ProblemDetail` JSON body whose `detail` identifies the missing resource.

To verify in Swagger UI, execute GET for any resource with an ID that does not exist. For example, an absent vehicle ID yields a detail such as `Vehicle 999999 not found`. POST/PUT requests referencing missing customers, vehicles, or deliveries use the same global handler.


## ETag conditional GET requests

Successful GET requests for both collection and individual resources under `/api/*` include an `ETag` response header. `EtagConfiguration` registers `ApiEtagFilter`, which hashes the serialized response body and uses Spring’s ETag parser for header matching. Send the previous ETag in the `If-None-Match` request header:

- No header or a different ETag: `200 OK`, current representation, and its ETag.
- A matching ETag: `304 Not Modified`, the ETag, and no response body.
- After a representation changes: the old ETag no longer matches, so GET returns `200` with the new body and ETag.

The filter supports quoted validators, weak validators, lists of validators, and `If-None-Match: *` for successful GET responses. Missing resources still return 404, including when the header is `*`. POST, PUT, and DELETE behavior is unchanged; this implements conditional reads, not optimistic concurrency control for writes. Because the validator is computed after producing the response body, this saves network bandwidth rather than database queries or serialization work.

### Test ETags in Swagger UI

1. Execute a GET, such as `/api/vehicles/{id}`, without `If-None-Match`. Copy the response's `etag` header, including the quotes.
2. Paste that value into the GET operation's optional **If-None-Match** header field and execute again. Expect **304** with an empty body.
3. Update that vehicle's capacity through PUT. Repeat GET with the old ETag: expect **200**, the updated vehicle, and a different ETag.
4. Repeat with the new ETag: expect **304**. The same workflow works for customers, deliveries, scenarios, and their list endpoints.


## JSON and XML content negotiation

The customer resource supports `application/json` and `application/xml`. The `Accept` header chooses the response format; `Content-Type` describes the body sent with POST/PUT. JSON is the default when no format is requested. Jackson XML is configured automatically by the `tools.jackson.dataformat:jackson-dataformat-xml` dependency, and the customer DTOs define the XML root element as `customer`.

Example XML customer request:

```xml
<customer>
  <name>Ana Popescu</name>
  <email>ana.xml@example.com</email>
  <phone>+40712345678</phone>
  <address>1 Example Street</address>
  <coordinates>44.4268,26.1025</coordinates>
</customer>
```

In Swagger UI, choose `application/xml` in the customer POST/PUT request-body media-type selector to send XML. Choose `application/xml` in the successful response media-type selector to request XML, or `application/json` for JSON. GET supports both formats for individual customers and customer lists. Unsupported response formats return 406; unsupported request content types return 415. Bean Validation applies to both XML and JSON requests.

GET responses include `Vary: Accept`. ETags are calculated from the selected representation, so the JSON and XML versions have different validators. Reuse an XML ETag with `Accept: application/xml` to receive 304; sending a JSON ETag while requesting XML returns 200 with the XML body.


## Springdoc documentation and Swagger UI verification

`CustomerController.findById` explicitly documents `GET /api/customers/{id}` using `@Operation`, `@Parameter`, and `@ApiResponses`. It describes the path ID, JSON/XML response schemas, 400 and 404 errors, and conditional GET behavior. `EtagConfiguration` adds the optional `If-None-Match` header, the ETag response header, and the 304 response to the generated OpenAPI document.

Verified on 2026-10-31 by executing requests through Swagger UI's **Try it out** / **Execute** controls:

| Test | Observed result |
| --- | --- |
| GET customer 1 with JSON selected | 200, customer JSON, ETag, and `Vary: Accept` |
| Repeat GET with its ETag in `If-None-Match` | 304, ETag retained, no response body |
| GET customer 1 with XML selected | 200, `<customer>` XML, and a different ETag |
| GET customer 999999999 | 404, ProblemDetail identifying the missing customer |
| Enter `abc` as the path ID | Swagger client-side validation rejects it as a non-integer |

The verification uses existing demo data and does not modify database records. Swagger may label a 304 response as an error in its result panel; the 304 with no body is the expected HTTP conditional-GET result.


## Database migrations with Flyway

Spring Boot's Flyway starter and the PostgreSQL Flyway module run migrations automatically before JPA initializes:

1. `V1__create_delivery_domain.sql`: depots, drivers, vehicles, customers, deliveries, and their indexes/constraints.
2. `V2__add_routing_scenarios.sql`: scenarios and the vehicle/delivery membership tables and indexes.

Flyway stores applied versions, checksums, and execution results in `flyway_schema_history`. Subsequent starts validate the recorded checksums and apply only pending migrations. Hibernate remains configured with `ddl-auto=validate`; basic SQL initialization is disabled, and `schema.sql` has been replaced by the versioned files. DataFaker still populates application data after migrations and JPA initialization.

### Existing lab4 database: one-time adoption

The local lab4 database was adopted and verified on 2026-10-31. Start it normally; the following procedure is only needed for another legacy database without Flyway history.

A database previously initialized by `schema.sql` has tables but no Flyway history. For that database only, run once from `lab4`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.flyway.baseline-on-migrate=true --spring.flyway.baseline-version=0"
```

Flyway records baseline version 0 and executes V1 and V2. These initial adoption migrations use `IF NOT EXISTS`, preserving the existing lab4 tables and records; missing tables are created. They expect the legacy lab4 schema to match the project mappings. Hibernate checks those mappings afterward. Baseline is an adoption marker, not a schema comparison or a backup.

After this one-time adoption, stop the application and use the normal run command. Automatic baselining is disabled by default. An empty database needs no adoption flags: both migrations run normally. Do not delete your database volume to enable migrations.

To inspect migration history in the local Docker database:

```bash
docker compose exec db psql -U lab4 -d lab4 -c 'SELECT installed_rank, version, description, type, success FROM flyway_schema_history ORDER BY installed_rank;'
```

For later schema changes, add `V3__describe_change.sql`, then V4, and so on. Do not edit already applied migrations: checksum validation detects changes and prevents startup. Flyway clean is disabled to protect existing data.


Migration verification on 2026-10-31 passed 15 checks: fresh V1/V2 application, repeat migration with no pending work, Hibernate validation on a fresh schema, rejection of an unbaselined nonempty schema, explicit baseline-0 adoption, preservation of legacy rows, checksum mismatch detection, adoption of the existing local lab4 schema, and normal startup afterward. Row counts and ordered row-content hashes for all eight domain/join tables were identical before and after adopting the local database. Verification schemas were removed afterward.


## Customer HATEOAS links

Every customer returned by GET (including collection items), POST, and PUT includes a `_links` map:

```json
{
  "id": 1,
  "name": "Demo Customer",
  "_links": {
    "self": {"href": "http://localhost:8080/api/v2/customers/1"},
    "customers": {"href": "http://localhost:8080/api/v2/customers"},
    "deliveries": {"href": "http://localhost:8080/api/v2/customers/1/deliveries"}
  }
}
```

The example omits the other customer fields for readability. These links enable navigation using relation names instead of constructing resource URLs. `CustomerRepresentationAssembler` builds absolute links from the current servlet mapping and the selected API version, so the host, port, and context path are not hardcoded. DTOs carry a small `ResourceLink` value; no entities are serialized and no HAL-specific media type is required. The existing JSON customer-array shape is preserved, and XML customer responses include the same navigation data.

`GET /api/customers/{id}/deliveries` is the working target of the `deliveries` link. It returns only that customer's deliveries, ordered by ID, using delivery DTOs. An existing customer with no deliveries gets an empty collection; a missing customer gets 404. It supports JSON/XML negotiation and ETags like the other customer GET endpoints.

In Swagger UI, execute GET for a customer, inspect `_links`, then execute the linked `GET /api/customers/{id}/deliveries` operation with that ID. Every returned delivery should have the same `customerId`. POST/PUT customer responses also contain the navigation links.


## URI-based API versioning and migration strategy

All existing operations for customers, vehicles, deliveries, and scenarios are available under both `/api/v1/...` and `/api/v2/...`, including customer delivery navigation. V2 is preferred for new clients. Both versions currently accept and return the same DTO contract; versioning does not require inventing a breaking payload change. They use the same service layer, database, and resource IDs, so a record created in v1 is visible in v2.

| Route | Contract | Status |
| --- | --- | --- |
| `/api/customers` (and other original resource paths) | v1 compatibility alias | Deprecated, still supported |
| `/api/v1/customers` | v1 | Deprecated, still supported |
| `/api/v2/customers` | v2, currently payload-compatible with v1 | Preferred |

The same URI prefixes apply to `vehicles`, `deliveries`, and `scenarios`. Unknown versions such as `/api/v3/customers` have no mapping and return 404; they never silently fall back to another version. There are no automatic redirects for POST/PUT/DELETE.

`API-Version: 1` or `API-Version: 2` identifies the selected version in responses. V1 and legacy responses also include a standards-formatted `Deprecation` timestamp and a `Link` header with `rel="successor-version"` pointing at the corresponding v2 URI. No retirement date has been scheduled, so no `Sunset` date is advertised. Customer HATEOAS links remain in the requested version; legacy aliases return canonical v1 links. Creation `Location` headers identify the route used to create the resource.

### Swagger documentation

Select **v1** or **v2** in Swagger UI's definition selector. The versioned OpenAPI documents are `/v3/api-docs/v1` and `/v3/api-docs/v2`; the ungrouped `/v3/api-docs` still lists all mappings. Conditional GET headers and 304 responses are documented for both groups. JSON/XML content negotiation for customers is independent of URI versioning.

### Transition clients from v1 to v2

1. Inventory callers using `/api/...` or `/api/v1/...`. Keep v1 behavior stable during migration; publish the v2 schema and release notes before any future breaking change.
2. Change the client's configured base prefix to `/api/v2`, keeping IDs, HTTP methods, request bodies, and accepted media types unchanged for this release. Follow the v2 links returned by the server rather than constructing paths manually.
3. Treat v2 URIs as separate cache keys. Make an initial unconditional GET and store its ETag; do not carry cached v1 customer representations into v2 because their navigation links differ.
4. Verify create/read/update/delete, validation, errors, related deliveries, content negotiation, and conditional GET against the v2 Swagger definition. Use the `API-Version` response header to confirm routing. Move callers gradually; avoid replaying writes in both versions because they share data.
5. If a breaking v2 change is later needed, first split its controllers and DTOs from v1 instead of changing the currently shared DTOs or mappings. Keep business logic reusable, map each version at the HTTP boundary, and provide explicit before/after payload examples. Use additive Flyway migrations while both versions are deployed; remove old columns only after v1 retirement.
6. Measure remaining v1 usage at the gateway or access logs. Once callers are migrated, announce a retirement date and add a `Sunset` header before removing old routes. Removal is a future explicit release change; it has not been enabled here.

Rollback during this compatible transition means restoring the client's `/api/v1` prefix; existing data and IDs remain usable. Future breaking versions need their own rollback and data-compatibility plan.


Versioning verification on 2026-10-31 passed 135 live checks, covering both versions and legacy routes, shared-data CRUD, version/deprecation metadata, version-aware links, JSON/XML, conditional GET, unknown-version rejection, and scoped Swagger documents. The verification used a non-default context path containing `/api` to check link construction and removed its test records afterward.
