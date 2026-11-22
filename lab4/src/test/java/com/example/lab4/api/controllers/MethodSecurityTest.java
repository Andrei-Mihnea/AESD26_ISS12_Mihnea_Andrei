package com.example.lab4.api.controllers;

import com.example.lab4.application.cqrs.CommandBus;
import com.example.lab4.application.cqrs.QueryBus;
import com.example.lab4.application.commands.customers.*;
import com.example.lab4.application.commands.vehicles.*;
import com.example.lab4.application.commands.deliveries.*;
import com.example.lab4.application.commands.scenarios.*;
import com.example.lab4.application.queries.customers.*;
import com.example.lab4.application.queries.vehicles.*;
import com.example.lab4.application.queries.deliveries.*;
import com.example.lab4.application.queries.scenarios.*;
import com.example.lab4.api.mapping.CustomerRepresentationAssembler;
import com.example.lab4.api.configuration.MethodSecurityConfiguration;
import com.example.lab4.application.dto.*;
import com.example.lab4.domain.entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Direct calls to real Spring proxies: no HTTP security filters or database are involved. */
@SpringJUnitConfig(MethodSecurityTest.TestConfiguration.class)
class MethodSecurityTest {
    @Configuration
    @Import({MethodSecurityConfiguration.class, CustomerController.class, DeliveryController.class,
            VehicleController.class, ScenarioController.class, CustomerRepresentationAssembler.class})
    static class TestConfiguration {
        @Bean CommandBus commands() { return mock(CommandBus.class); }
        @Bean QueryBus queries() {
            QueryBus bus = mock(QueryBus.class);
            when(bus.execute(any(GetCustomersQuery.class))).thenReturn(List.of());
            when(bus.execute(any(GetVehiclesQuery.class))).thenReturn(List.of());
            when(bus.execute(any(GetDeliveriesQuery.class))).thenReturn(List.of());
            when(bus.execute(any(GetScenariosQuery.class))).thenReturn(List.of());
            return bus;
        }
    }

    @Autowired CustomerController customers;
    @Autowired DeliveryController deliveries;
    @Autowired VehicleController vehicles;
    @Autowired ScenarioController scenarios;
    @Autowired CommandBus commands;
    @Autowired QueryBus queries;

    private final CustomerRequest customer = new CustomerRequest("Test", "test@example.com", "+40123456789", "Address", "44,26");
    private final VehicleRequest vehicle = new VehicleRequest("Test", 10, true);
    private final DeliveryRequest delivery = new DeliveryRequest(1L, 2, "Test", OffsetDateTime.parse("2026-11-07T12:00:00Z"));
    private final ScenarioRequest scenario = new ScenarioRequest("Test", Set.of(), Set.of());

    enum Identity { ADMIN, CUSTOMER, DRIVER, ANONYMOUS, MISSING, UNAUTHENTICATED, UNPREFIXED_ADMIN, UNKNOWN_ROLE }

    @BeforeEach
    void prepare() {
        SecurityContextHolder.clearContext();
        reset(commands, queries);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest("POST", "/api/v2/test")));
        User user = User.customer(customer.name(), customer.email(), customer.phone(), customer.address(), customer.coordinates());
        ReflectionTestUtils.setField(user, "id", 1L);
        when(commands.execute(any(CreateCustomerCommand.class))).thenReturn(user);
        when(commands.execute(any(UpdateCustomerCommand.class))).thenReturn(Optional.of(user));
        when(commands.execute(any(DeleteCustomerCommand.class))).thenReturn(true);
        when(queries.execute(any(FindCustomerByIdQuery.class))).thenReturn(Optional.of(user));
        when(queries.execute(any(GetCustomersQuery.class))).thenReturn(List.of());
        when(queries.execute(any(GetVehiclesQuery.class))).thenReturn(List.of());
        when(queries.execute(any(GetDeliveriesQuery.class))).thenReturn(List.of());
        when(queries.execute(any(GetScenariosQuery.class))).thenReturn(List.of());
        when(queries.execute(any(GetCustomerDeliveriesQuery.class))).thenReturn(List.of());
        when(commands.execute(any(CreateDeliveryCommand.class))).thenReturn(new DeliveryResponse(1L, 1L, 2, "Test", delivery.timestamp()));
        when(commands.execute(any(CreateVehicleCommand.class))).thenReturn(new VehicleResponse(1L, "Test", 10, true));
        when(commands.execute(any(CreateScenarioCommand.class))).thenReturn(new ScenarioResponse(1L, "Test", Set.of(), Set.of()));
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
        RequestContextHolder.resetRequestAttributes();
    }

    private void signIn(Identity identity) {
        if (identity == Identity.MISSING) return;
        var authentication = switch (identity) {
            case ANONYMOUS -> new AnonymousAuthenticationToken("test", "anonymousUser", List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
            case UNAUTHENTICATED -> UsernamePasswordAuthenticationToken.unauthenticated("user", "unused");
            case UNPREFIXED_ADMIN -> UsernamePasswordAuthenticationToken.authenticated("user", null, List.of(new SimpleGrantedAuthority("ADMIN")));
            default -> UsernamePasswordAuthenticationToken.authenticated("user", null, List.of(new SimpleGrantedAuthority("ROLE_" + identity.name())));
        };
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private List<Runnable> writes() {
        return List.of(
                () -> customers.create(customer), () -> customers.update(1L, customer),
                () -> deliveries.create(delivery), () -> deliveries.update(1L, delivery),
                () -> vehicles.create(vehicle), () -> vehicles.update(1L, vehicle),
                () -> scenarios.create(scenario), () -> scenarios.update(1L, scenario));
    }

    private void assertDenied(Runnable invocation) {
        RuntimeException error = assertThrows(RuntimeException.class, invocation::run);
        assertTrue(error instanceof AccessDeniedException || error instanceof AuthenticationCredentialsNotFoundException,
                () -> "Expected method security rejection, got " + error.getClass().getName());
    }

    @ParameterizedTest
    @EnumSource(Identity.class)
    void applicationRolesCanCreateAndUpdate(Identity identity) {
        signIn(identity);
        if (Set.of(Identity.ADMIN, Identity.CUSTOMER, Identity.DRIVER).contains(identity)) {
            writes().forEach(invocation -> assertDoesNotThrow(invocation::run));
            verify(commands).execute(new CreateCustomerCommand(customer));
            verify(commands).execute(new UpdateCustomerCommand(1L, customer.name(), customer.email(), customer.phone(), customer.address(), customer.coordinates()));
            verify(commands).execute(new CreateDeliveryCommand(delivery));
            verify(commands).execute(new UpdateDeliveryCommand(1L, delivery));
            verify(commands).execute(new CreateVehicleCommand(vehicle));
            verify(commands).execute(new UpdateVehicleCommand(1L, vehicle));
            verify(commands).execute(new CreateScenarioCommand(scenario));
            verify(commands).execute(new UpdateScenarioCommand(1L, scenario));
        } else {
            writes().forEach(this::assertDenied);
            verifyNoInteractions(commands, queries);
        }
    }

    @ParameterizedTest
    @EnumSource(Identity.class)
    void onlyAdminCanDelete(Identity identity) {
        signIn(identity);
        List<Runnable> deletes = List.of(() -> customers.delete(1L), () -> deliveries.delete(1L),
                () -> vehicles.delete(1L), () -> scenarios.delete(1L));
        if (identity == Identity.ADMIN) {
            deletes.forEach(invocation -> assertDoesNotThrow(invocation::run));
            verify(commands).execute(new DeleteCustomerCommand(1L));
            verify(commands).execute(new DeleteDeliveryCommand(1L));
            verify(commands).execute(new DeleteVehicleCommand(1L));
            verify(commands).execute(new DeleteScenarioCommand(1L));
        } else {
            deletes.forEach(this::assertDenied);
            verifyNoInteractions(commands, queries);
        }
    }

    @ParameterizedTest
    @EnumSource(Identity.class)
    void customerAndDeliveryReadsRequireAuthentication(Identity identity) {
        signIn(identity);
        List<Runnable> reads = List.of(() -> customers.findById(1L),
                () -> customers.findDeliveries(1L), () -> deliveries.findById(1L));
        if (Set.of(Identity.MISSING, Identity.ANONYMOUS, Identity.UNAUTHENTICATED).contains(identity)) {
            reads.forEach(this::assertDenied);
            verifyNoInteractions(commands, queries);
        } else {
            reads.forEach(invocation -> assertDoesNotThrow(invocation::run));
        }
    }

    @ParameterizedTest
    @EnumSource(Identity.class)
    void onlyAdminCanListAllResources(Identity identity) {
        signIn(identity);
        List<Runnable> lists = List.of(() -> customers.findAll(), () -> deliveries.findAll(),
                () -> vehicles.findAll(), () -> scenarios.findAll());
        if (identity == Identity.ADMIN) {
            lists.forEach(invocation -> assertDoesNotThrow(invocation::run));
            verify(queries).execute(new GetCustomersQuery());
            verify(queries).execute(new GetDeliveriesQuery());
            verify(queries).execute(new GetVehiclesQuery());
            verify(queries).execute(new GetScenariosQuery());
        } else {
            lists.forEach(this::assertDenied);
            verifyNoInteractions(commands, queries);
        }
    }

    @Test
    void vehicleAndScenarioReadsStayPublic() {
        assertDoesNotThrow(() -> vehicles.findById(1L));
        assertDoesNotThrow(() -> scenarios.findById(1L));
        verify(queries).execute(new GetVehicleByIdQuery(1L));
        verify(queries).execute(new GetScenarioByIdQuery(1L));
    }
}
