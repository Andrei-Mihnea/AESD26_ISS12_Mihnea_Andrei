package com.example.lab3.repository;

import com.example.lab3.model.Book;
import com.example.lab3.model.BookType;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BookRepository {
    private final Map<UUID, Book> books = new HashMap<>();

    public BookRepository() {
        add(new Book("Clean Code", BookType.NON_FICTION));
        add(new Book("Effective Java", BookType.NON_FICTION));
        add(new Book("The Hobbit", BookType.FICTION));
        add(new Book("Library Encyclopedia", BookType.REFERENCE));
    }

    private void add(Book book) {
        books.put(book.getId(), book);
    }

    public Optional<Book> findById(UUID id) {
        return Optional.ofNullable(books.get(id));
    }

    public void save(Book book) {
        books.put(book.getId(), book);
    }

    public java.util.Collection<Book> findAll() {
        return java.util.Collections.unmodifiableCollection(books.values());
    }
}
