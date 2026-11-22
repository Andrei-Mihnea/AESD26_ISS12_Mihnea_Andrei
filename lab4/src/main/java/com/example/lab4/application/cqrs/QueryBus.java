package com.example.lab4.application.cqrs;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** In-process dispatch to the matching Spring-managed handler; transaction/cache proxies remain active. */
@Component
public class QueryBus {
    private final Map<Class<?>, QueryHandler<?, ?>> handlers = new HashMap<>();
    public QueryBus(List<QueryHandler<?, ?>> registeredHandlers) {
        for (var handler : registeredHandlers) {
            if (handlers.put(handler.messageType(), handler) != null) {
                throw new IllegalStateException("Duplicate handler for " + handler.messageType().getName());
            }
        }
    }
    @SuppressWarnings("unchecked")
    public <R> R execute(Query<R> message) {
        var handler = (QueryHandler<Query<R>, R>) handlers.get(message.getClass());
        if (handler == null) throw new IllegalArgumentException("No handler for " + message.getClass().getName());
        return handler.handle(message);
    }
}
