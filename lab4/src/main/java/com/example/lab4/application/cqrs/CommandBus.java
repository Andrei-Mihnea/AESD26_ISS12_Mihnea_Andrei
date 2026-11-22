package com.example.lab4.application.cqrs;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommandBus {
    private final Map<Class<?>, CommandHandler<?, ?>> handlers = new HashMap<>();
    public CommandBus(List<CommandHandler<?, ?>> registeredHandlers) {
        for (var handler : registeredHandlers) {
            if (handlers.put(handler.messageType(), handler) != null) {
                throw new IllegalStateException("Duplicate handler for " + handler.messageType().getName());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <R> R execute(Command<R> message) {
        var handler = (CommandHandler<Command<R>, R>) handlers.get(message.getClass());
        if (handler == null) throw new IllegalArgumentException("No handler for " + message.getClass().getName());
        return handler.handle(message);
    }
}
