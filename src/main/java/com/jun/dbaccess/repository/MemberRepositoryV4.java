package com.jun.dbaccess.repository;

import com.jun.dbaccess.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.SQLException;

@Slf4j
@RequiredArgsConstructor
public class MemberRepositoryV4 {
    private final JdbcTemplate jdbcTemplate;

    public void save(Member member) {
        String sql =  "insert into member(member_id, money) values(?,?)";
        int affectCount = jdbcTemplate.update(sql, member.getMemberId(), member.getMoney());
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id = ?";
        jdbcTemplate.update(sql, memberId);

    }

    public Member findById(String id) throws SQLException {
        String sql = "select * from member where member_id = ?";
        return jdbcTemplate.queryForObject(sql, memberRowMapper(), id);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> {
            Member member = new Member();
            member.setMemberId(rs.getString("member_id"));
            member.setMoney(rs.getInt("money"));
            return member;
        };

    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money = ? where member_id = ?";
        jdbcTemplate.update(sql, money, memberId);
    }
}
