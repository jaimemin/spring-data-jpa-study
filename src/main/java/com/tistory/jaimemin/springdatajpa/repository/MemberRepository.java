package com.tistory.jaimemin.springdatajpa.repository;

import com.tistory.jaimemin.springdatajpa.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * NamedQuery는 사실 많이 쓰이지는 않음
     *
     * @param username
     * @return
     */
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

}
