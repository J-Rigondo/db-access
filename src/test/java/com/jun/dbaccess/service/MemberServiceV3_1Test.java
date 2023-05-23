package com.jun.dbaccess.service;

import com.jun.dbaccess.domain.Member;
import com.jun.dbaccess.repository.MemberRepositoryV1;
import com.jun.dbaccess.repository.MemberRepositoryV3;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

import static com.jun.dbaccess.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class MemberServiceV3_1Test {
    public static final String A = "memberA";
    public static final String B = "memberB";
    public static final String EX = "ex";
    @Autowired
    private MemberRepositoryV3 memberRepositoryV3;
    @Autowired
    private MemberServiceV3_3 memberServiceV3_3;

    //테스트에서 빈 주입
    @TestConfiguration
    static class TestConfig {
        @Bean
        DataSource dataSource() {
            return new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        }

        @Bean
        PlatformTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        MemberRepositoryV3 memberRepositoryV3() {
            return new MemberRepositoryV3(dataSource());
        }

        @Bean
        MemberServiceV3_3 memberServiceV3_3() {
            return new MemberServiceV3_3(memberRepositoryV3());
        }
    }

//    @BeforeEach
//    void before() {
//        DriverManagerDataSource ds = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
//        memberRepositoryV3 = new MemberRepositoryV3(ds);
//
//        DataSourceTransactionManager tx = new DataSourceTransactionManager(ds);
//        memberServiceV3_1 = new MemberServiceV3_1(tx,memberRepositoryV3);
//    }

    @AfterEach
    void after() throws SQLException {
        memberRepositoryV3.delete(A);
        memberRepositoryV3.delete(B);
        memberRepositoryV3.delete(EX);
    }

    @Test
    void aopCheck() {
        log.info("service={}", memberServiceV3_3.getClass());
        log.info("repo={}", memberRepositoryV3.getClass());
    }
    @Test
    @DisplayName("이체 중 예외 발생")
    void accountTransferEx() throws SQLException {
        //given
        Member memberA = new Member(A, 10000);
        Member memberB = new Member(EX, 10000);
        memberRepositoryV3.save(memberA);
        memberRepositoryV3.save(memberB);

        //when
        assertThatThrownBy(
                () -> memberServiceV3_3.accountTransfer(memberA.getMemberId(), memberB.getMemberId(), 2000)
        ).isInstanceOf(IllegalStateException.class);

        //then
        Member findA = memberRepositoryV3.findById(memberA.getMemberId());
        Member findB = memberRepositoryV3.findById(memberB.getMemberId());

        assertThat(findA.getMoney()).isEqualTo(10000);
        assertThat(findB.getMoney()).isEqualTo(10000);

    }
}