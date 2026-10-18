package com.example.lab3;

import com.example.lab3.service.BorrowingService;
import com.example.lab3.exception.BorrowingException;
import com.example.lab3.repository.BookRepository;
import com.example.lab3.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Lab3Application {

    public static void main(String[] args) {
        SpringApplication.run(Lab3Application.class, args);
    }

    @Bean
    CommandLineRunner demonstrate(
            BorrowingService borrowingService,
            MemberRepository memberRepository,
            BookRepository bookRepository) {
        return args -> {
            var member = memberRepository.findAll().iterator().next();
            var book = bookRepository.findAll().iterator().next();

            borrowingService.borrowBook(member.getId(), book.getId());

            try {
                borrowingService.borrowBook(member.getId(), book.getId());
            } catch (BorrowingException exception) {
                System.out.println("Caught borrowing exception: " + exception.getMessage());
            }

            borrowingService.returnBook(member.getId(), book.getId());
        };
    }
}
