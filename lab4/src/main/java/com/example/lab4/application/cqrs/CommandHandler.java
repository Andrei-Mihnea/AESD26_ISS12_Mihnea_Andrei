package com.example.lab4.application.cqrs;

public interface CommandHandler<C extends Command<R>, R> {
    Class<C> messageType();
    R handle(@jakarta.validation.Valid C message);
}
