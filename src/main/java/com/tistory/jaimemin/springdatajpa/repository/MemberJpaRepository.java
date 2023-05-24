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

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return entityManager.createQuery("SELECT m FROM Member m WHERE m.username = :username and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }

    /**
     * NamedQuery는 컴파일 시점에 오류 잡을 수 있는게 장점
     *
     * @param username
     * @return
     */
    public List<Member> findByUsername(String username) {
        return entityManager.createNamedQuery("Member.findByUsername", Member.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<Member> findByPage(int age, int offset, int limit) {
        return entityManager.createQuery("SELECT m FROM Member m WHERE m.age = :age ORDER BY m.username DESC")
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCount(int age) {
        return entityManager.createQuery("SELECT COUNT(m) FROM Member m WHERE m.age = :age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }

    public int bulkAgePlus(int age) {
        return entityManager.createQuery("UPDATE Member m SET m.age = m.age + 1 WHERE m.age >= :age")
                .setParameter("age", age)
                .executeUpdate();
    }
}
