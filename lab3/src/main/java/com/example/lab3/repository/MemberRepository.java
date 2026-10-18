package com.example.lab3.repository;

import com.example.lab3.model.Member;
import com.example.lab3.model.MemberRole;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MemberRepository {
    private final Map<UUID, Member> members = new HashMap<>();

    public MemberRepository() {
        add(new Member("Ana", MemberRole.STUDENT));
        add(new Member("Mihai", MemberRole.REGULAR));
        add(new Member("Ioana", MemberRole.PREMIUM));
    }

    private void add(Member member) {
        members.put(member.getId(), member);
    }

    public Optional<Member> findById(UUID id) {
        return Optional.ofNullable(members.get(id));
    }
    public void save(Member member) {
        members.put(member.getId(), member);
    }

    public java.util.Collection<Member> findAll() {
        return java.util.Collections.unmodifiableCollection(members.values());
    }
}
