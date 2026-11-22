package com.example.lab4.application.cqrs;

public interface QueryHandler<C extends Query<R>, R> {
    Class<C> messageType();
    R handle(C message);
}
