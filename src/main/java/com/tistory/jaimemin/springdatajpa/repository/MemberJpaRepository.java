package com.tistory.jaimemin.springdatajpa.repository;

import com.tistory.jaimemin.springdatajpa.entity.Member;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Member save(Member member) {
        entityManager.persist(member);

        return member;
    }

    public void delete(Member member) {
        entityManager.remove(member);
    }

    public List<Member> findAll() {
        // JPQL
        return entityManager.createQuery("SELECT m FROM Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = entityManager.find(Member.class, id);

        return Optional.ofNullable(member);
    }

    public long count() {
        return entityManager.createQuery("SELECT COUNT(m) FROM Member m", Long.class)
                .getSingleResult();
    }

    public Member find(Long id) {
        return entityManager.find(Member.class, id);
    }
}
