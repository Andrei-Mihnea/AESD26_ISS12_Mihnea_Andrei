package com.example.lab4.application;

import com.example.lab4.application.commands.customers.CreateCustomerCommand;
import com.example.lab4.application.commands.customers.CreateCustomerCommandHandler;
import com.example.lab4.application.queries.customers.FindCustomerByEmailQuery;
import com.example.lab4.application.queries.customers.FindCustomerByEmailQueryHandler;
import com.example.lab4.application.dto.CustomerRequest;
import com.example.lab4.application.cqrs.CommandBus;
import com.example.lab4.application.cqrs.QueryBus;
import com.example.lab4.domain.enums.Role;
import com.example.lab4.domain.entities.User;
import com.example.lab4.application.interfaces.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(CustomerCqrsCacheTest.Configuration.class)
class CustomerCqrsCacheTest {
    @org.springframework.context.annotation.Configuration
    @EnableCaching(proxyTargetClass = true)
    @Import({CreateCustomerCommandHandler.class, FindCustomerByEmailQueryHandler.class, CommandBus.class, QueryBus.class})
    static class Configuration {
        @Bean UserRepository users() { return mock(UserRepository.class); }
        @Bean CacheManager cacheManager() { return new ConcurrentMapCacheManager("customersByEmail"); }
    }
    @Autowired CommandBus commands;
    @Autowired QueryBus queries;
    @Autowired UserRepository users;

    @Test void commandInvalidatesTheQueryCacheAcrossSeparateBeans() {
        String email = "cqrs@example.com";
        when(users.findByEmailAndRole(email, Role.CUSTOMER)).thenReturn(Optional.empty());
        assertTrue(queries.execute(new FindCustomerByEmailQuery(email)).isEmpty());
        assertTrue(queries.execute(new FindCustomerByEmailQuery(email)).isEmpty());
        verify(users).findByEmailAndRole(email, Role.CUSTOMER);
        User created = User.customer("Customer", email, "123", "Address", "44,26");
        when(users.save(any(User.class))).thenReturn(created);
        commands.execute(new CreateCustomerCommand(new CustomerRequest("Customer", email, "123", "Address", "44,26")));
        when(users.findByEmailAndRole(email, Role.CUSTOMER)).thenReturn(Optional.of(created));
        assertEquals(created, queries.execute(new FindCustomerByEmailQuery(email)).orElseThrow());
        verify(users, times(2)).findByEmailAndRole(email, Role.CUSTOMER);
    }
}
