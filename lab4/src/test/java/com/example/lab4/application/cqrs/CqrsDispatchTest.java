package com.example.lab4.application.cqrs;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class CqrsDispatchTest {
    record SetNameCommand(String name) implements Command<String> {}
    record GetNameQuery() implements Query<String> {}

    static class SetNameCommandHandler implements CommandHandler<SetNameCommand, String> {
        private final AtomicReference<String> state;
        SetNameCommandHandler(AtomicReference<String> state) { this.state = state; }
        public Class<SetNameCommand> messageType() { return SetNameCommand.class; }
        public String handle(SetNameCommand command) { state.set(command.name()); return command.name(); }
    }
    static class GetNameQueryHandler implements QueryHandler<GetNameQuery, String> {
        private final AtomicReference<String> state;
        GetNameQueryHandler(AtomicReference<String> state) { this.state = state; }
        public Class<GetNameQuery> messageType() { return GetNameQuery.class; }
        public String handle(GetNameQuery query) { return state.get(); }
    }

    @Test void commandsAndQueriesDispatchToMatchingHandlersWithTypedResults() {
        var state = new AtomicReference<>("before");
        var commands = new CommandBus(List.of(new SetNameCommandHandler(state)));
        var queries = new QueryBus(List.of(new GetNameQueryHandler(state)));
        assertEquals("after", commands.execute(new SetNameCommand("after")));
        assertEquals("after", queries.execute(new GetNameQuery()));
    }
    @Test void duplicateHandlersFailAtRegistration() {
        var state = new AtomicReference<>("name");
        var command = new SetNameCommandHandler(state);
        var query = new GetNameQueryHandler(state);
        assertThrows(IllegalStateException.class, () -> new CommandBus(List.of(command, command)));
        assertThrows(IllegalStateException.class, () -> new QueryBus(List.of(query, query)));
    }
    @Test void missingHandlersFailExplicitly() {
        assertThrows(IllegalArgumentException.class, () -> new CommandBus(List.of()).execute(new SetNameCommand("name")));
        assertThrows(IllegalArgumentException.class, () -> new QueryBus(List.of()).execute(new GetNameQuery()));
    }
}
