package com.tistory.jaimemin.springdatajpa.repository;

import com.tistory.jaimemin.springdatajpa.entity.Member;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * *Impl과 같은 클래스로 선언해야 사용자 정의 Repository 구현
 */
@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository{

    private final EntityManager entityManager;

    @Override
    public List<Member> findMemberCustom() {
        return entityManager.createQuery("SELECT m FROM Member m")
                .getResultList();
    }
}
