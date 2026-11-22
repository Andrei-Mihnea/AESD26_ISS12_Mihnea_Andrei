package com.example.optimizationworker.application.commands;

import com.example.optimizationworker.domain.entities.OptimizationJob;
import com.example.vros.messaging.OptimizationAcknowledgment.VehicleRoute;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

/** Builds capacity-constrained tours by repeatedly visiting the nearest feasible unassigned delivery. */
@Component
public class GreedyVrpSolver {
    private static final double EARTH_RADIUS_KM = 6_371.0088;

    public Optional<List<VehicleRoute>> solve(OptimizationJob job) {
        return solve(job, (completed, total) -> {});
    }

    public Optional<List<VehicleRoute>> solve(OptimizationJob job, BiConsumer<Integer, Integer> progress) {
        // Use high-capacity vehicles first so large-demand deliveries remain assignable.
        List<OptimizationJob.Vehicle> vehicles = job.vehicles().stream()
                .sorted(Comparator.comparingInt(OptimizationJob.Vehicle::capacity).reversed()
                        .thenComparing(OptimizationJob.Vehicle::id, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        List<OptimizationJob.Delivery> deliveries = job.deliveries();
        validateUniqueIds(vehicles, deliveries);

        List<DeliveryPoint> unassigned = new ArrayList<>();
        for (var delivery : deliveries) {
            if (delivery.id() == null || delivery.demand() <= 0) {
                throw new IllegalArgumentException("Every delivery needs an ID and positive demand");
            }
            unassigned.add(new DeliveryPoint(delivery, coordinate(delivery.coordinates())));
        }
        List<VehicleRoute> routes = new ArrayList<>();
        for (var vehicle : vehicles) {
            if (vehicle.id() == null || vehicle.capacity() <= 0) {
                throw new IllegalArgumentException("Every vehicle needs an ID and positive capacity");
            }
            Coordinate depot = coordinate(vehicle.depotCoordinates());
            Coordinate current = depot;
            int remainingCapacity = vehicle.capacity();
            int totalDemand = 0;
            double distanceKm = 0;
            List<Long> stops = new ArrayList<>();

            while (true) {
                Coordinate from = current;
                int availableCapacity = remainingCapacity;
                DeliveryPoint next = unassigned.stream()
                        .filter(point -> point.delivery().demand() <= availableCapacity)
                        .min(Comparator.comparingDouble((DeliveryPoint point) -> distanceKm(from, point.location()))
                                .thenComparing(point -> point.delivery().id()))
                        .orElse(null);
                if (next == null) break;

                distanceKm += distanceKm(current, next.location());
                current = next.location();
                remainingCapacity -= next.delivery().demand();
                totalDemand += next.delivery().demand();
                stops.add(next.delivery().id());
                unassigned.remove(next);
                progress.accept(deliveries.size() - unassigned.size(), deliveries.size());
            }

            if (!stops.isEmpty()) {
                // Each vehicle returns to its own depot after its final delivery.
                distanceKm += distanceKm(current, depot);
                routes.add(new VehicleRoute(vehicle.id(), stops, totalDemand, round(distanceKm)));
            }
        }

        return unassigned.isEmpty() ? Optional.of(List.copyOf(routes)) : Optional.empty();
    }

    private static void validateUniqueIds(List<OptimizationJob.Vehicle> vehicles,
                                          List<OptimizationJob.Delivery> deliveries) {
        Set<Long> vehicleIds = new HashSet<>();
        for (var vehicle : vehicles) {
            if (vehicle.id() != null && !vehicleIds.add(vehicle.id())) {
                throw new IllegalArgumentException("Vehicle IDs must be unique");
            }
        }
        Set<Long> deliveryIds = new HashSet<>();
        for (var delivery : deliveries) {
            if (delivery.id() != null && !deliveryIds.add(delivery.id())) {
                throw new IllegalArgumentException("Delivery IDs must be unique");
            }
        }
    }

    private static Coordinate coordinate(String value) {
        if (value == null) throw new IllegalArgumentException("Coordinates are required");
        String[] components = value.trim().split(",", -1);
        if (components.length != 2) throw new IllegalArgumentException("Coordinates must use 'latitude,longitude' format");
        try {
            double latitude = Double.parseDouble(components[0].trim());
            double longitude = Double.parseDouble(components[1].trim());
            if (!Double.isFinite(latitude) || !Double.isFinite(longitude)
                    || latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
                throw new IllegalArgumentException("Coordinates are out of range");
            }
            return new Coordinate(latitude, longitude);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Coordinates must be numeric", exception);
        }
    }

    private static double distanceKm(Coordinate first, Coordinate second) {
        double lat1 = Math.toRadians(first.latitude());
        double lat2 = Math.toRadians(second.latitude());
        double deltaLat = lat2 - lat1;
        double deltaLon = Math.toRadians(second.longitude() - first.longitude());
        double haversine = Math.pow(Math.sin(deltaLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(deltaLon / 2), 2);
        return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
    }

    private static double round(double value) { return Math.round(value * 100.0) / 100.0; }

    private record Coordinate(double latitude, double longitude) {}
    private record DeliveryPoint(OptimizationJob.Delivery delivery, Coordinate location) {}
}
