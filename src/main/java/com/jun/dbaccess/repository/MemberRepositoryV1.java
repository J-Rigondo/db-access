package com.jun.dbaccess.repository;

import com.jun.dbaccess.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DataSource, JdbcUtils
 */
@RequiredArgsConstructor
@Slf4j
public class MemberRepositoryV1 {
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
//            pstmt.close(); //여기서 에러 발생하면 아래 con close 실행이 안됨
//            con.close();
            close(con, pstmt, null);
        }
    }

    public Member findById(String id) throws SQLException {
        String sql = "select * from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
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
            close(con, pstmt, null);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;

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
//            pstmt.close(); //여기서 에러 발생하면 아래 con close 실행이 안됨
//            con.close();
            close(con, pstmt, null);
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
//            pstmt.close(); //여기서 에러 발생하면 아래 con close 실행이 안됨
//            con.close();
            close(con, pstmt, null);
        }

    }

    public Member findById(Connection con, String id) throws SQLException {
        String sql = "select * from member where member_id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;

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
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            //connection을 닫으면 안됨,
        }
    }

    public void update(Connection con, String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";

        PreparedStatement pstmt = null;

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
//            pstmt.close(); //여기서 에러 발생하면 아래 con close 실행이 안됨
//            con.close();
            JdbcUtils.closeStatement(pstmt);
            //connection을 닫으면 안됨,
        }

    }

    private Connection getConnection() throws SQLException {
        Connection conn = dataSource.getConnection();
        log.info("con:{}",conn);
        return dataSource.getConnection();
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(con);
    }
}
