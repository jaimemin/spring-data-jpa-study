package com.tistory.jaimemin.springdatajpa.repository;

public interface NestedClosedProjections {

    String getUsername();

    // LEFT JOIN
    // Team은 최적화 안되는 문제
    TeamInfo getTeam();

    interface TeamInfo {

        String getName();
    }
}
