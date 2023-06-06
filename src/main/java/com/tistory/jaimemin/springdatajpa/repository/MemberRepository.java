package com.tistory.jaimemin.springdatajpa.repository;

import com.tistory.jaimemin.springdatajpa.dto.MemberDto;
import com.tistory.jaimemin.springdatajpa.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long>, CustomMemberRepository {

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * NamedQuery는 사실 많이 쓰이지는 않음
     *
     * @param username
     * @return
     */
    @Query(name = "Member.findByUsername")
    List<Member> findByUsername(@Param("username") String username);

    /**
     * 컴파일 시점에 파싱해서 문법 오류 체크
     */
    @Query("SELECT m FROM Member m WHERE m.username = :username AND m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("SELECT m.username FROM Member m")
    List<String> findUsernameList();

    @Query("SELECT new com.tistory.jaimemin.springdatajpa.dto.MemberDto(m.id, m.username, t.name) FROM Member m JOIN m.team t")
    List<MemberDto> findMemberDto();

    @Query("SELECT m FROM Member m WHERE m.username in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    // 컬렉션
    List<Member> findListByUsername(String username);

    // 단건
    Member findMemberByUsername(String username);

    // Optional
    Optional<Member> findOptionalByUsername(String username);

    @Query(value = "SELECT m FROM Member m LEFT JOIN m.team t", countQuery = "SELECT COUNT(m) FROM Member m")
    Page<Member> findByAge(int age, Pageable pageable);

    /**
     * Read가 아닐 때 Modifying 필수
     *
     * @param age
     * @return
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Member m SET m.age = m.age + 1 WHERE m.age >= :age")
    int bulkAgePlus(@Param("age") int age);

    /**
     * FETCH JOIN
     */
    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.team")
    List<Member> findMemberFetchJoin();

    /**
     * FETCH JOIN JPQL의 대안
     * EntityGraph는 JPA에서 제공하는 기능
     */
    @Override
    @EntityGraph(attributePaths = ("team"))
    List<Member> findAll();

    @Query("SELECT m FROM Member m")
    @EntityGraph(attributePaths = ("team"))
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = ("team"))
    List<Member> findEntityGraphByUsername(@Param("username") String username);

    /**
     * 거의 안 쓰이고 성능 테스트 후 필요 시
     * 요즘 GC도 잘되어있고 굳이 이 정도 최적화까지는... 대규모 트래픽 서비스라면 고려
     *
     * 조회 성능이 진짜 딸린다면 앞단에 redis 같은 캐시를 배치하는 것이 최우선
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    // SELECT FOR UPDATE (LOCK)

    /**
     * 실시간 조회가 많은 서비스는 해당 lock 방식 권장 X
     * Optimistic Lock으로 풀어야 함
     *
     * @param username
     * @return
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);

    List<UsernameOnlyDto> findProjectionsByUsername(@Param("username") String username);

    <T> List<T> findGenericProjectionsByUsername(@Param("username") String username, Class<T> type);

    @Query(value = "SELECT * FROM member WHERE username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    @Query(value = "SELECT m.member_id as id, m.username, t.name as teamName FROM member m LEFT JOIN team t"
            , countQuery = "SELECT COUNT(*) FROM member"
            , nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}
