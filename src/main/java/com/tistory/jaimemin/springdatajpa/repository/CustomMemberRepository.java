package com.tistory.jaimemin.springdatajpa.repository;

import com.tistory.jaimemin.springdatajpa.entity.Member;

import java.util.List;

public interface CustomMemberRepository {

    List<Member> findMemberCustom();
}
