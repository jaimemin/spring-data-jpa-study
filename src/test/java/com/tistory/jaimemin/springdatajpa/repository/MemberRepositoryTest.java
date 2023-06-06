package com.tistory.jaimemin.springdatajpa.repository;

import com.tistory.jaimemin.springdatajpa.dto.MemberDto;
import com.tistory.jaimemin.springdatajpa.entity.Member;
import com.tistory.jaimemin.springdatajpa.entity.Team;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MemberRepository memberRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member memberA = new Member("memberA");
        Member memberB = new Member("memberB");
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        Member findMemberA = memberRepository.findById(memberA.getId()).get();
        Member findMemberB = memberRepository.findById(memberB.getId()).get();
        assertThat(findMemberA).isEqualTo(memberA);
        assertThat(findMemberB).isEqualTo(memberB);

        List<Member> members = memberRepository.findAll();
        assertThat(members.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        memberRepository.delete(memberA);
        memberRepository.delete(memberB);

        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member member = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);

        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
    }

    @Test
    public void testNamedQuery() {
        Member member = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);

        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void testQuery() {
        Member member = new Member("AAA", 10);
        memberRepository.save(member);

        List<Member> result = memberRepository.findUser("AAA", 10);

        assertThat(result.get(0)).isEqualTo(member);
    }

    @Test
    public void findUsernameList() {
        Member member = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member);
        memberRepository.save(member2);

        List<String> usernameList = memberRepository.findUsernameList();

        assertThat(usernameList.get(0)).isEqualTo("AAA");
        assertThat(usernameList.get(1)).isEqualTo("BBB");
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member = new Member("AAA", 10);
        member.setTeam(team);
        memberRepository.save(member);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        assertThat(memberDto.get(0).getUsername()).isEqualTo(member.getUsername());
        assertThat(memberDto.get(0).getId()).isEqualTo(member.getId());
        assertThat(memberDto.get(0).getTeamName()).isEqualTo(team.getName());
    }

    @Test
    public void findByNames() {
        Member member = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void returnType() {
        Member member = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member);
        memberRepository.save(member2);

        List<Member> aaa = memberRepository.findListByUsername("AAA"); // 얘는 없으면 empty list
        Member findMember = memberRepository.findMemberByUsername("AAA"); // 얘는 없으면 null
        Optional<Member> optionalMember = memberRepository.findOptionalByUsername("AAA"); // java8부터는 optional로 통일

        assertThat(aaa.size()).isEqualTo(1);
        assertThat(findMember).isEqualTo(member);
        assertThat(optionalMember.get()).isEqualTo(member);
    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        /**
         * Page 객체를 사용할 때 주의 점: Count Query 호출할 때 성능 저하 발생할 수도
         * @Query 어노테이션을 통해 count query 분리 필요 (성능 테스트 해보고 join이 너무 많이 호출되면 count query 별도 분리하는 것을 추천)
         */
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        /**
         * 외부에 반환할 때는 DTO로 변환 필요
         */
        Page<MemberDto> dtos = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));

        // then
        assertThat(page.getContent().size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() {
        // given
        // 영속성 컨텍스트에 아직 있는데
        memberRepository.save(new Member("member", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        /**
         * bulk 연산은 persistence context 무시해버리고 그냥 DB에 바로 반영해버림
         * 따라서 persistence context는 반영되었는지 모름
         * persistence context clear 필요
         * clear 안하면 findMember age 40이라고 조회됨
         *
         * JpaRepository에서 Modifying 애노테이션에서 clearAutomatically true 주면 동일하게 동작
         */
        //
        int resultCount = memberRepository.bulkAgePlus(20);
//        entityManager.flush();
//        entityManager.clear();

        List<Member> result = memberRepository.findByUsername("member5");
        Member findMember = result.get(0);
        assertThat(findMember.getAge()).isEqualTo(41);

        // then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        // given
        // memberA -> teamA
        // memberB -> teamB
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member memberA = new Member("memberA", 10, teamA);
        Member memberB = new Member("memberB", 10, teamB);

        entityManager.flush();
        entityManager.clear();

        // when
        List<Member> members = memberRepository.findAll();

        // 이대로 두면 N + 1 문제 발생 가능성
        for (Member member : members) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getName()); // Lazy Loading이기 때문에 proxy class 불러옴
            System.out.println("member.team = " + member.getTeam().getName()); // 이제서야 lazy loading db query 호출
        }

        // Query 한번에 다 가지고옴
        List<Member> fetchJoinMembers = memberRepository.findMemberFetchJoin();

        for (Member member : fetchJoinMembers) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getName()); // Lazy Loading이기 때문에 proxy class 불러옴
            System.out.println("member.team = " + member.getTeam().getName()); // 이제서야 lazy loading db query 호출
        }

        // Query 한번에 다 가지고옴
        List<Member> entityGraphByUsernames = memberRepository.findEntityGraphByUsername("memberA");

        for (Member member : entityGraphByUsernames) {
            System.out.println("member = " + member.getUsername());
            System.out.println("member.teamClass = " + member.getTeam().getName()); // Lazy Loading이기 때문에 proxy class 불러옴
            System.out.println("member.team = " + member.getTeam().getName()); // 이제서야 lazy loading db query 호출
        }
    }

    @Test
    public void queryHint() {
        // given
        Member memberA = new Member("memberA", 10);
        memberRepository.save(memberA);
        entityManager.flush(); // 여기까지는 Persistence Context에 남아있고 DB에 반영만
        entityManager.clear(); // Persistence Context 클리어

        // when
        /**
         * read only라고 생각하고 snap shot 안 만듬
         * 변경 감지(dirty checking) 안함
         */
        Member findMember = memberRepository.findReadOnlyByUsername("memberA");
        findMember.setUsername("memberB");

        entityManager.flush();
    }

    @Test
    public void lock() {
        // given
        Member memberA = new Member("memberA", 10);
        memberRepository.save(memberA);
        entityManager.flush(); // 여기까지는 Persistence Context에 남아있고 DB에 반영만
        entityManager.clear(); // Persistence Context 클리어

        // when
        /**
         * select
         *         member0_.member_id as member_i1_0_,
         *         member0_.age as age2_0_,
         *         member0_.team_id as team_id4_0_,
         *         member0_.username as username3_0_
         *     from
         *         member member0_
         *     where
         *         member0_.username=? for update
         */
        List<Member> members = memberRepository.findLockByUsername("memberA");
    }

    @Test
    public void callCustom() {
        List<Member> members = memberRepository.findMemberCustom();
    }

    @Test
    public void queryByExample() {
        // given
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member member = new Member("member", 0, teamA);
        Member member2 = new Member("member2", 0, teamA);
        entityManager.persist(member);
        entityManager.persist(member2);

        entityManager.flush();
        entityManager.clear();

        // when
        // Probe
        Member sampleMember = new Member("member");
        Team team = new Team("teamA");
        sampleMember.setTeam(team);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase("age");
        Example<Member> example = Example.of(sampleMember, matcher);

        List<Member> members = memberRepository.findAll(example);

        assertThat(members.get(0).getUsername()).isEqualTo("member");
    }

    @Test
    public void projections() {
        // given
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member member = new Member("member", 0, teamA);
        Member member2 = new Member("member2", 0, teamA);
        entityManager.persist(member);
        entityManager.persist(member2);

        entityManager.flush();
        entityManager.clear();

        // when
        List<UsernameOnlyDto> result = memberRepository.findProjectionsByUsername("m1");

        for (UsernameOnlyDto usernameOnlyDto : result) {
            System.out.println("usernameOnlyDto.username = " + usernameOnlyDto.getUsername());
        }
    }

    @Test
    public void genericProjections() {
        // given
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member member = new Member("member", 0, teamA);
        Member member2 = new Member("member2", 0, teamA);
        entityManager.persist(member);
        entityManager.persist(member2);

        entityManager.flush();
        entityManager.clear();

        // when
        List<UsernameOnlyDto> result = memberRepository.findGenericProjectionsByUsername("m1", UsernameOnlyDto.class);

        for (UsernameOnlyDto usernameOnlyDto : result) {
            System.out.println("usernameOnlyDto.username = " + usernameOnlyDto.getUsername());
        }
    }

    @Test
    public void nestedProjections() {
        // given
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member member = new Member("member", 0, teamA);
        Member member2 = new Member("member2", 0, teamA);
        entityManager.persist(member);
        entityManager.persist(member2);

        entityManager.flush();
        entityManager.clear();

        // when
        List<NestedClosedProjections> result = memberRepository.findGenericProjectionsByUsername("m1", NestedClosedProjections.class);

        for (NestedClosedProjections nestedClosedProjections : result) {
            String username = nestedClosedProjections.getUsername();
            System.out.println("username = " + username);
            String teamName = nestedClosedProjections.getTeam().getName();
            System.out.println("teamName = " + teamName); // nested 엔티티는 최적화가 안되는 문제점 (중첩구조에서는 한계가 있음)
        }
    }

    @Test
    public void nativeQuery() {
        // given
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member member = new Member("member", 0, teamA);
        Member member2 = new Member("member2", 0, teamA);
        entityManager.persist(member);
        entityManager.persist(member2);

        entityManager.flush();
        entityManager.clear();

        // when
        Member result = memberRepository.findByNativeQuery("m1");

        System.out.println("result = " + result);
    }

    @Test
    public void nativePageQuery() {
        // given
        Team teamA = new Team("teamA");
        entityManager.persist(teamA);

        Member member = new Member("member", 0, teamA);
        Member member2 = new Member("member2", 0, teamA);
        entityManager.persist(member);
        entityManager.persist(member2);

        entityManager.flush();
        entityManager.clear();

        // when
        Page<MemberProjection> result = memberRepository.findByNativeProjection(PageRequest.of(0, 10));

        for (MemberProjection memberProjection : result.getContent()) {
            System.out.println("memberProjection.username = " + memberProjection.getUsername());
            System.out.println("memberProjection.teamName = " + memberProjection.getTeamName());
        }
    }


    /**
     * 실무에서 사용하기에는 너무 복잡한  specification
     * 쓰지 말자!
     *
     * -> QueryDSL이 대안
     */
//    @Test
//    public void specBasic() {
//        // given
//        Team teamA = new Team("teamA");
//        entityManager.persist(teamA);
//
//        Member member = new Member("member", 0, teamA);
//        Member member2 = new Member("member2", 0, teamA);
//        entityManager.persist(member);
//        entityManager.persist(member2);
//
//        entityManager.flush();
//        entityManager.clear();
//
//        // when
//        Specification<Member> spec = MemberSpec.username("m1").and(MemberSpec.teamName("teamA"));
//        List<Member> result = memberRepository.findAll(spec);
//
//        assertThat(result.size()).isEqualTo(1);
//    }
}