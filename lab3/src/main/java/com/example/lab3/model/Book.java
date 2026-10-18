package com.example.lab3.model;

import java.util.UUID;

public class Book {
    private final UUID id;
    private final String title;
    private final BookType type;
    private boolean available;

    public Book(String title) {
        this(title, BookType.NON_FICTION);
    }

    public Book(String title, BookType type) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.type = type;
        this.available = true;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public BookType getType() {
        return type;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
