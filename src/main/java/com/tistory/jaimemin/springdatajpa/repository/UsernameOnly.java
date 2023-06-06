package com.tistory.jaimemin.springdatajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UsernameOnly {

    // Spring SPL 문법
    // @Value("#{target.username + ' ' + target.age}")
    String getUsername();
}
