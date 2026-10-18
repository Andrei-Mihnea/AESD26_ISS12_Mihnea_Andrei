package com.example.lab3.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Member {
    private final UUID id;
    private final String name;

    private final MemberRole role;
    private final List<Book>  borrowedBooks = new ArrayList<>();

    public Member(String name, MemberRole role) {
        id = UUID.randomUUID();
        this.name = name;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MemberRole getRole() {
        return role;
    }

    public List<Book> getBorrowedBooks() {
        return Collections.unmodifiableList(borrowedBooks);
    }

    public void borrow(Book book) {
        borrowedBooks.add(book);
    }

    public void returnBook(Book book) {
        borrowedBooks.remove(book);
    }

    public boolean hasBorrowed(Book book) {
        return borrowedBooks.contains(book);
    }
}
