package com.example.reservation.repository;

import com.example.reservation.domain.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

    //member 테이블을 조회할 때 관계된 테이블을 같이 조회 하는 sql
    @Query(value = "select m from member m left join fetch m.roles where m.userName = :userName")
    Optional<MemberEntity> findByUserName(String userName);

    boolean existsByUserName(String userName);
}
