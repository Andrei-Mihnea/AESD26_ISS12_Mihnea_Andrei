package com.example.lab3.service;

import com.example.lab3.event.MemberBorrowingLimitEvent;
import com.example.lab3.exception.BorrowingException;
import com.example.lab3.model.Book;
import com.example.lab3.model.Member;
import com.example.lab3.policy.BorrowingPolicy;
import com.example.lab3.repository.BookRepository;
import com.example.lab3.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BorrowingService {
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final BorrowingPolicy borrowingPolicy;
    private final ApplicationEventPublisher eventPublisher;
    private final int eventThreshold;

    public BorrowingService(
            MemberRepository memberRepository,
            BookRepository bookRepository,
            BorrowingPolicy borrowingPolicy,
            ApplicationEventPublisher eventPublisher,
            @Value("${library.event-threshold:2}") int eventThreshold) {
        this.memberRepository = memberRepository;
        this.bookRepository = bookRepository;
        this.borrowingPolicy = borrowingPolicy;
        this.eventPublisher = eventPublisher;
        this.eventThreshold = eventThreshold;
    }

    public void borrowBook(UUID memberId, UUID bookId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BorrowingException(
                        "Member with ID " + memberId + " does not exist"
                ));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BorrowingException(
                        "Book with ID " + bookId + " does not exist"
                ));

        book.setAvailable(false);
        member.borrow(book);

        int borrowedCount = member.getBorrowedBooks().size();
        if (borrowedCount > eventThreshold) {
            eventPublisher.publishEvent(
                    new MemberBorrowingLimitEvent(member.getName(), borrowedCount)
            );
        }

        System.out.println(member.getName() + " borrowed " + book.getTitle());
    }

    public void returnBook(UUID memberId, UUID bookId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BorrowingException(
                        "Member with ID " + memberId + " does not exist"
                ));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BorrowingException(
                        "Book with ID " + bookId + " does not exist"
                ));

        if (!member.hasBorrowed(book)) {
            throw new BorrowingException(
                    member.getName() + " has not borrowed " + book.getTitle()
            );
        }

        member.returnBook(book);
        book.setAvailable(true);

        System.out.println(member.getName() + " returned " + book.getTitle());
    }

    // Used by the aspects to look up details for logging.
    public Member findMember(UUID memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BorrowingException(
                        "Member with ID " + memberId + " does not exist"
                ));
    }

    public Book findBook(UUID bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BorrowingException(
                        "Book with ID " + bookId + " does not exist"
                ));
    }

    public BorrowingPolicy getBorrowingPolicy() {
        return borrowingPolicy;
    }
}