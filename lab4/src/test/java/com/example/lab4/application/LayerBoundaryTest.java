package com.example.lab4.application;

import com.example.lab4.application.commands.authentication.LoginCommandHandler;
import com.example.lab4.application.commands.authentication.LogoutCommandHandler;
import com.example.lab4.application.commands.authentication.RefreshAccessTokenCommandHandler;
import com.example.lab4.application.commands.authentication.RegisterUserCommandHandler;
import com.example.lab4.application.commands.customers.CreateCustomerCommandHandler;
import com.example.lab4.application.commands.customers.DeleteCustomerByIdCommandHandler;
import com.example.lab4.application.commands.customers.DeleteCustomerCommandHandler;
import com.example.lab4.application.commands.customers.SaveCustomerCommandHandler;
import com.example.lab4.application.commands.customers.SaveCustomersCommandHandler;
import com.example.lab4.application.commands.customers.UpdateCustomerCommandHandler;
import com.example.lab4.application.commands.deliveries.CreateDeliveryCommandHandler;
import com.example.lab4.application.commands.deliveries.DeleteDeliveryByIdCommandHandler;
import com.example.lab4.application.commands.deliveries.DeleteDeliveryCommandHandler;
import com.example.lab4.application.commands.deliveries.SaveDeliveriesCommandHandler;
import com.example.lab4.application.commands.deliveries.SaveDeliveryCommandHandler;
import com.example.lab4.application.commands.deliveries.UpdateDeliveryCommandHandler;
import com.example.lab4.application.commands.depots.DeleteDepotCommandHandler;
import com.example.lab4.application.commands.depots.SaveDepotCommandHandler;
import com.example.lab4.application.commands.depots.SaveDepotsCommandHandler;
import com.example.lab4.application.commands.drivers.DeleteDriverCommandHandler;
import com.example.lab4.application.commands.drivers.SaveDriverCommandHandler;
import com.example.lab4.application.commands.drivers.SaveDriversCommandHandler;
import com.example.lab4.application.commands.scenarios.CreateScenarioCommandHandler;
import com.example.lab4.application.commands.scenarios.DeleteScenarioByIdCommandHandler;
import com.example.lab4.application.commands.scenarios.DeleteScenarioCommandHandler;
import com.example.lab4.application.commands.scenarios.SaveScenarioCommandHandler;
import com.example.lab4.application.commands.scenarios.SubmitScenarioOptimizationCommandHandler;
import com.example.lab4.application.commands.scenarios.UpdateScenarioCommandHandler;
import com.example.lab4.application.commands.users.ConfigureUserLoginCommandHandler;
import com.example.lab4.application.commands.users.CreateAdministratorCommandHandler;
import com.example.lab4.application.commands.users.DisableUserLoginCommandHandler;
import com.example.lab4.application.commands.vehicles.CreateVehicleCommandHandler;
import com.example.lab4.application.commands.vehicles.DeleteVehicleByIdCommandHandler;
import com.example.lab4.application.commands.vehicles.DeleteVehicleCommandHandler;
import com.example.lab4.application.commands.vehicles.SaveVehicleCommandHandler;
import com.example.lab4.application.commands.vehicles.SaveVehiclesCommandHandler;
import com.example.lab4.application.commands.vehicles.UpdateVehicleAvailabilityCommandHandler;
import com.example.lab4.application.commands.vehicles.UpdateVehicleCommandHandler;
import com.example.lab4.application.queries.customers.FindCustomerByEmailQueryHandler;
import com.example.lab4.application.queries.customers.FindCustomerByIdQueryHandler;
import com.example.lab4.application.queries.customers.GetCustomersQueryHandler;
import com.example.lab4.application.queries.deliveries.DeliveryExistsForCustomerWithNotesQueryHandler;
import com.example.lab4.application.queries.deliveries.FindDeliveryByIdQueryHandler;
import com.example.lab4.application.queries.deliveries.GetCustomerDeliveriesQueryHandler;
import com.example.lab4.application.queries.deliveries.GetDeliveriesQueryHandler;
import com.example.lab4.application.queries.deliveries.GetDeliveryByIdQueryHandler;
import com.example.lab4.application.queries.deliveries.ListDeliveryEntitiesQueryHandler;
import com.example.lab4.application.queries.deliveries.SearchDeliveriesQueryHandler;
import com.example.lab4.application.queries.depots.FindDepotByIdQueryHandler;
import com.example.lab4.application.queries.depots.FindDepotByNameQueryHandler;
import com.example.lab4.application.queries.depots.GetDepotsQueryHandler;
import com.example.lab4.application.queries.drivers.FindDriverByEmailQueryHandler;
import com.example.lab4.application.queries.drivers.FindDriverByIdQueryHandler;
import com.example.lab4.application.queries.drivers.GetDriversQueryHandler;
import com.example.lab4.application.queries.scenarios.FindScenarioByIdQueryHandler;
import com.example.lab4.application.queries.scenarios.GetScenarioByIdQueryHandler;
import com.example.lab4.application.queries.scenarios.GetScenariosQueryHandler;
import com.example.lab4.application.queries.scenarios.ListScenarioEntitiesQueryHandler;
import com.example.lab4.application.queries.users.GetUserByIdQueryHandler;
import com.example.lab4.application.queries.users.GetUsersByRoleQueryHandler;
import com.example.lab4.application.queries.users.GetUsersQueryHandler;
import com.example.lab4.application.queries.vehicles.FindVehicleByIdQueryHandler;
import com.example.lab4.application.queries.vehicles.FindVehicleByNameQueryHandler;
import com.example.lab4.application.queries.vehicles.GetAvailableVehiclesAtDepotQueryHandler;
import com.example.lab4.application.queries.vehicles.GetVehicleByIdQueryHandler;
import com.example.lab4.application.queries.vehicles.GetVehiclesQueryHandler;
import com.example.lab4.application.queries.vehicles.ListVehicleEntitiesQueryHandler;
import com.example.lab4.application.interfaces.*;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LayerBoundaryTest {
    @Test void repositoryPortsDoNotExtendSpringDataInterfaces() {
        for (Class<?> port : List.of(UserRepository.class, DepotRepository.class, VehicleRepository.class,
                DeliveryRepository.class, ScenarioRepository.class, RefreshTokenRepository.class)) {
            assertEquals(0, port.getInterfaces().length, port.getName());
        }
    }
    @Test void queriesDeclareReadOnlyTransactions() {
        for (Class<?> query : List.of(ListVehicleEntitiesQueryHandler.class,
                FindVehicleByIdQueryHandler.class,
                FindVehicleByNameQueryHandler.class,
                GetAvailableVehiclesAtDepotQueryHandler.class,
                GetVehiclesQueryHandler.class,
                GetVehicleByIdQueryHandler.class,
                GetDriversQueryHandler.class,
                FindDriverByIdQueryHandler.class,
                FindDriverByEmailQueryHandler.class,
                ListScenarioEntitiesQueryHandler.class,
                FindScenarioByIdQueryHandler.class,
                GetScenariosQueryHandler.class,
                GetScenarioByIdQueryHandler.class,
                ListDeliveryEntitiesQueryHandler.class,
                FindDeliveryByIdQueryHandler.class,
                SearchDeliveriesQueryHandler.class,
                DeliveryExistsForCustomerWithNotesQueryHandler.class,
                GetDeliveriesQueryHandler.class,
                GetCustomerDeliveriesQueryHandler.class,
                GetDeliveryByIdQueryHandler.class,
                GetUsersQueryHandler.class,
                GetUsersByRoleQueryHandler.class,
                GetUserByIdQueryHandler.class,
                GetCustomersQueryHandler.class,
                FindCustomerByIdQueryHandler.class,
                FindCustomerByEmailQueryHandler.class,
                GetDepotsQueryHandler.class,
                FindDepotByIdQueryHandler.class,
                FindDepotByNameQueryHandler.class)) {
            assertTrue(query.getAnnotation(Transactional.class).readOnly(), query.getName());
        }
    }
    @Test void handlersDependOnInterfacesInsteadOfInfrastructureImplementations() {
        for (Class<?> handler : List.of(SaveDepotCommandHandler.class,
                SaveDepotsCommandHandler.class,
                DeleteDepotCommandHandler.class,
                SaveDeliveryCommandHandler.class,
                SaveDeliveriesCommandHandler.class,
                DeleteDeliveryByIdCommandHandler.class,
                DeleteDeliveryCommandHandler.class,
                CreateDeliveryCommandHandler.class,
                UpdateDeliveryCommandHandler.class,
                SaveVehicleCommandHandler.class,
                SaveVehiclesCommandHandler.class,
                DeleteVehicleByIdCommandHandler.class,
                UpdateVehicleAvailabilityCommandHandler.class,
                DeleteVehicleCommandHandler.class,
                CreateVehicleCommandHandler.class,
                UpdateVehicleCommandHandler.class,
                SaveDriverCommandHandler.class,
                SaveDriversCommandHandler.class,
                DeleteDriverCommandHandler.class,
                LoginCommandHandler.class,
                RefreshAccessTokenCommandHandler.class,
                LogoutCommandHandler.class,
                CreateCustomerCommandHandler.class,
                SaveCustomerCommandHandler.class,
                SaveCustomersCommandHandler.class,
                DeleteCustomerByIdCommandHandler.class,
                UpdateCustomerCommandHandler.class,
                DeleteCustomerCommandHandler.class,
                ConfigureUserLoginCommandHandler.class,
                DisableUserLoginCommandHandler.class,
                CreateAdministratorCommandHandler.class,
                SubmitScenarioOptimizationCommandHandler.class,
                SaveScenarioCommandHandler.class,
                DeleteScenarioByIdCommandHandler.class,
                DeleteScenarioCommandHandler.class,
                CreateScenarioCommandHandler.class,
                UpdateScenarioCommandHandler.class,
                RegisterUserCommandHandler.class,
                ListVehicleEntitiesQueryHandler.class,
                FindVehicleByIdQueryHandler.class,
                FindVehicleByNameQueryHandler.class,
                GetAvailableVehiclesAtDepotQueryHandler.class,
                GetVehiclesQueryHandler.class,
                GetVehicleByIdQueryHandler.class,
                GetDriversQueryHandler.class,
                FindDriverByIdQueryHandler.class,
                FindDriverByEmailQueryHandler.class,
                ListScenarioEntitiesQueryHandler.class,
                FindScenarioByIdQueryHandler.class,
                GetScenariosQueryHandler.class,
                GetScenarioByIdQueryHandler.class,
                ListDeliveryEntitiesQueryHandler.class,
                FindDeliveryByIdQueryHandler.class,
                SearchDeliveriesQueryHandler.class,
                DeliveryExistsForCustomerWithNotesQueryHandler.class,
                GetDeliveriesQueryHandler.class,
                GetCustomerDeliveriesQueryHandler.class,
                GetDeliveryByIdQueryHandler.class,
                GetUsersQueryHandler.class,
                GetUsersByRoleQueryHandler.class,
                GetUserByIdQueryHandler.class,
                GetCustomersQueryHandler.class,
                FindCustomerByIdQueryHandler.class,
                FindCustomerByEmailQueryHandler.class,
                GetDepotsQueryHandler.class,
                FindDepotByIdQueryHandler.class,
                FindDepotByNameQueryHandler.class)) {
            for (var field : handler.getDeclaredFields()) {
                String type = field.getType().getName();
                assertFalse(type.startsWith("com.example.lab4.infrastructure."), handler.getName() + " -> " + type);
                assertFalse(type.startsWith("org.springframework.amqp."), handler.getName() + " -> " + type);
                assertFalse(type.startsWith("org.springframework.data."), handler.getName() + " -> " + type);
            }
        }
    }
}
