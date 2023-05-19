package com.jun.dbaccess.repository;

import com.jun.dbaccess.connection.ConnectionConst;
import com.jun.dbaccess.connection.DBConnectionUtil;
import com.jun.dbaccess.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static com.jun.dbaccess.connection.ConnectionConst.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class MemberRepositoryV0Test {
    MemberRepositoryV0 repositoryV0 = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        Member memberV0 = new Member("memberV5", 10000);
        repositoryV0.save(memberV0);

        Member findMember = repositoryV0.findById(memberV0.getMemberId());
        System.out.println("findMember = " + findMember);

        assertThat(findMember).isEqualTo(memberV0); //객체 equals메소드로 비교

        repositoryV0.update(memberV0.getMemberId(), 20000);
        Member updMember = repositoryV0.findById(memberV0.getMemberId());

        assertThat(updMember.getMoney()).isEqualTo(20000);

    }

    @Test
    void dataSourceDriverManager() throws SQLException {
        //DriverManagerDataSource - 항상 새로운 커넥션 획득
        DriverManagerDataSource ds = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(ds);
    }

    @Test
    void connectionPool() throws SQLException {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(URL);
        ds.setUsername(USERNAME);
        ds.setPassword(PASSWORD);
        ds.setMaximumPoolSize(10);
        ds.setPoolName("JUNPOOL");

        useDataSource(ds);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        Connection connection1 = dataSource.getConnection();

        log.info("con:{}",connection);
        log.info("con:{}",connection1);


    }
}