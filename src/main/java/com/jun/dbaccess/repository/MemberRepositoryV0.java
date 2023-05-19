package com.jun.dbaccess.repository;

import com.jun.dbaccess.connection.DBConnectionUtil;
import com.jun.dbaccess.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager
 */
@Slf4j
public class MemberRepositoryV0 {
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

    private void close(Connection con, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("rs err",e);

            }
        }

        if (stmt != null) {
            try {
                stmt.close(); //여기서 에러 발생하면 아래 con close 실행이 안됨
            } catch (SQLException e) {
                log.info("stmt err",e);
            }

        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("con err",e);

            }

        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }


}
