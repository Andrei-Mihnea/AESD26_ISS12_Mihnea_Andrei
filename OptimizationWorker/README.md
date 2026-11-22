# OptimizationWorker

Separate Java 21 / Spring Boot project for the VROS optimization pipeline. It consumes VRP scenario requests from RabbitMQ, builds capacity-constrained tours with a greedy nearest-neighbor heuristic, and publishes correlated `SOLVED` or `UNROUTABLE` results.

## DDD and CQRS structure

```text
com.example.optimizationworker/
  domain/entities/               OptimizationJob
  application/commands/          AcceptOptimizationRequestCommand
                                 AcceptOptimizationRequestCommandHandler
                                 GreedyVrpSolver
  application/interfaces/        AcknowledgmentPublisher
  infrastructure/messaging/      Request/result publishers, request listener, DLQ handler
  infrastructure/configuration/  RabbitMQ topology and JSON converter
```

The Rabbit listener returns a future and dispatches optimization with `@Async` to a fixed-size `ThreadPoolTaskExecutor`. Listener concurrency matches the pool size, and QoS prefetch bounds unacknowledged work; additional jobs remain queued at RabbitMQ while those slots are busy. The solver assigns each vehicle the nearest unassigned delivery that fits its remaining capacity and estimates round-trip distance using Haversine distance. This deterministic heuristic does not guarantee a globally shortest solution. Integration JSON records remain under `com.example.vros.messaging` in both projects so they share the same broker contract.

## Run

Start PostgreSQL and RabbitMQ from the existing API project:

```bash
cd ../lab4
docker compose up -d --wait db rabbitmq
```

Run VROS in one terminal from `lab4`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

Run the worker in another terminal from `OptimizationWorker`:

```bash
./mvnw spring-boot:run
```

The worker has no HTTP server or database dependency. It connects directly to RabbitMQ. Both projects use `localhost:5672`, username `vros`, password `vros-password` for the local broker. Override with `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, and `RABBITMQ_PASSWORD`.

RabbitMQ management: http://localhost:15672 (same broker credentials).

## Test through Swagger

1. Open http://localhost:8080/swagger-ui/index.html and keep v2 selected.
2. Call public POST /login and paste accessToken into Authorize → bearerAuth.
3. Create/select a scenario containing vehicles and deliveries.
4. Execute POST /api/v2/scenarios/{id}/solve. It needs no request body and returns 202 with requestId, scenarioId, status SUBMITTED, and submittedAt.
5. The worker logs the received request and its published result. VROS stores the result under the same request ID. Poll GET /api/optimization-jobs/{requestId}/status and fetch GET /api/optimization-jobs/{requestId}/solution after it completes.

The endpoint also exists under `/api/scenarios/{id}/solve` and `/api/v1/scenarios/{id}/solve`. Existing POST permissions apply. Missing scenarios return 404; broker publication failures return 503.

## Messaging contract

- Durable direct exchange: `vros.optimization`
- Request routing key: `optimization.requested`; queue: `vros.optimization.requests.v2`
- Acknowledgment routing key: `optimization.acknowledged`; queue: `vros.optimization.acknowledgments`
- Progress routing key: `optimization.progress`; queue: `vros.optimization.progress`
- Failed requests route through `vros.optimization.dlx` to `vros.optimization.requests.dlq` after configured retries.

Worker retries transient request-processing failures up to `OPTIMIZATION_RETRY_MAX_ATTEMPTS` (default 3), with a linear delay of `OPTIMIZATION_RETRY_DELAY_MS` per attempt (default 200 ms). Invalid requests are rejected immediately. Exhausted failures are dead-lettered; the DLQ listener logs message ID, correlation ID, reason, and RabbitMQ `x-death` details, and publishes a correlated `FAILED` result when the request payload can be decoded.

Set `OPTIMIZATION_WORKER_POOL_SIZE` to cap parallel optimizations (default 4) and `OPTIMIZATION_WORKER_PREFETCH` to set RabbitMQ QoS prefetch (default 1). The executor queue is bounded to available prefetch slots. Every `OPTIMIZATION_METRICS_INTERVAL_MS` (default 10 seconds), logs report completed/failed jobs per second, average duration, active threads, and queue depth. RabbitMQ Management also exposes ready/unacknowledged queue counts for broker-side throughput observation.

Request JSON contains a request UUID, scenario ID/name, timestamp, selected vehicle IDs/capacities/depot coordinates, and delivery IDs/demands/customer coordinates. The request is a snapshot, so the worker does not query the API database. Result JSON contains requestId, scenarioId, status, acknowledgedAt, and routes. Each route contains vehicleId, deliveryIds in visit order, totalDemand, and estimated distanceKm. If no feasible assignment exists, status is `UNROUTABLE` and routes is empty.

Both projects declare the same topology, so either can start first. API request publication and worker result publication wait for RabbitMQ publisher confirmation. The listener container acknowledges incoming deliveries after successful handling. Delivery is at least once; requestId correlates repeated deliveries.

The small JSON contract records under `com.example.vros.messaging` are present in both independent projects; keep their fields and queue names aligned when changing the contract.

## Verification

Run `./mvnw test` in either project. Worker tests cover the correlated routing result, transient retry attempts, immediate rejection of invalid input, and DLQ failure reporting. API tests cover job state/result retrieval and scenario snapshot publication. Broker routing itself should be exercised with RabbitMQ running.
