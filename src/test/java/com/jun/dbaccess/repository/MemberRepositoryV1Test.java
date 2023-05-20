package com.jun.dbaccess.repository;

import com.jun.dbaccess.connection.ConnectionConst;
import com.jun.dbaccess.connection.DBConnectionUtil;
import com.jun.dbaccess.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static com.jun.dbaccess.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MemberRepositoryV1Test {
    MemberRepositoryV1 memberRepositoryV1;

    @BeforeEach
    void setUp() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);

        //connection pool
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(URL);
        ds.setUsername(USERNAME);
        ds.setPassword(PASSWORD);

        memberRepositoryV1 = new MemberRepositoryV1(ds);
    }

    @Test
    void crud() throws SQLException, InterruptedException {
        Member memberV0 = new Member("memberV9", 10000);
        memberRepositoryV1.save(memberV0);

        Member findMember = memberRepositoryV1.findById(memberV0.getMemberId());
        System.out.println("findMember = " + findMember);

        assertThat(findMember).isEqualTo(memberV0); //객체 equals메소드로 비교

        memberRepositoryV1.update(memberV0.getMemberId(), 20000);
        Member updMember = memberRepositoryV1.findById(memberV0.getMemberId());

        assertThat(updMember.getMoney()).isEqualTo(20000);

        Thread.sleep(1000);
    }


}