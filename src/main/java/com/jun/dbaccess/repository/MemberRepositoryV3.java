package com.jun.dbaccess.repository;

import com.jun.dbaccess.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV3 {
    private final DataSource dataSource;

    public void save(Member member) throws SQLException {
        String sql =  "insert into member(member_id, money) values(?,?)";
        Connection con = null;
        PreparedStatement pstmt = null;

        con = getConnection();
        try {
            //prepare를 사용해서 sql injection 방지
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            int affectedCount = pstmt.executeUpdate();
            log.info("affected count={}", affectedCount);

        } catch (SQLException e) {
            log.error("db error",e);
            throw e;
        } finally {
            close(con,pstmt,null);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

        con = getConnection();
        try {
            //prepare를 사용해서 sql injection 방지
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            int affectedCount = pstmt.executeUpdate();
            log.info("affected count={}", affectedCount);

        } catch (SQLException e) {
            log.error("db error",e);
            throw e;
        } finally {
            close(con,pstmt,null);
        }

    }

    public Member findById(String id) throws SQLException {
        String sql = "select * from member where member_id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        Connection con = getConnection();

        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member(rs.getString("member_id"), rs.getInt("money"));

                return member;
            } else {
                throw new NoSuchElementException("member not found id = "+id);
            }

        } catch (SQLException e) {
            log.error("db error",e);
            throw e;

        } finally {
            close(con,pstmt,rs);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";

        PreparedStatement pstmt = null;
        Connection con = getConnection();

        con = getConnection();
        try {
            //prepare를 사용해서 sql injection 방지
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int affectedCount = pstmt.executeUpdate();
            log.info("affected count={}", affectedCount);

        } catch (SQLException e) {
            log.error("db error",e);
            throw e;
        } finally {
            close(con,pstmt,null);
        }

    }

    private Connection getConnection() throws SQLException {
        //트랜잭션 동기화 매니저에서 조회
        Connection conn = DataSourceUtils.getConnection(dataSource);
        log.info("con:{}",conn);
        return dataSource.getConnection();
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        //트랜잭션 동기화 사용
        DataSourceUtils.releaseConnection(con, dataSource);
    }
}
