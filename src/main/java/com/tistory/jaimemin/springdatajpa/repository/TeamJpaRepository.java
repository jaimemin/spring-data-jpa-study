package com.tistory.jaimemin.springdatajpa.repository;

import com.tistory.jaimemin.springdatajpa.entity.Team;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TeamJpaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Team save(Team team) {
        entityManager.persist(team);

        return team;
    }

    public void delete(Team team) {
        entityManager.remove(team);
    }

    public List<Team> findAll() {
        return entityManager.createQuery("SELECT t FROM Team t", Team.class)
                .getResultList();
    }

    public Optional<Team> findById(Long id) {
        Team team = entityManager.find(Team.class, id);

        return Optional.ofNullable(team);
    }

    public long count() {
        return entityManager.createQuery("SELECT COUNT(t) FROM Team t", Long.class)
                .getSingleResult();
    }

    public Team find(Long id) {
        return entityManager.find(Team.class, id);
    }
}
