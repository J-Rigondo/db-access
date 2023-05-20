package com.jun.dbaccess.service;

import com.jun.dbaccess.domain.Member;
import com.jun.dbaccess.repository.MemberRepositoryV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@RequiredArgsConstructor
@Slf4j
public class MemberServiceV2 {
    private final DataSource dataSource;
    private final MemberRepositoryV1 memberRepositoryV1;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        Connection con = dataSource.getConnection();
        try {
            con.setAutoCommit(false);

            Member fromMember = memberRepositoryV1.findById(con, fromId);
            Member toMember = memberRepositoryV1.findById(con, toId);

            memberRepositoryV1.update(con, fromId, fromMember.getMoney() - money);

            validation(toMember);
            memberRepositoryV1.update(con, toId, toMember.getMoney() + money);

            con.commit();

        } catch (SQLException e) {
            con.rollback();
            throw new RuntimeException(e);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true); //connnection 풀에 반환할 때 다시 원복
                    con.close(); //풀에 반납

                } catch (Exception e) {
                    log.info("error", e);
                }
            }
        }


    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 예외 발생");
        }
    }

}
