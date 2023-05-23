package com.jun.dbaccess.service;

import com.jun.dbaccess.domain.Member;
import com.jun.dbaccess.repository.MemberRepositoryV1;
import com.jun.dbaccess.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class MemberServiceV3_1 {
    private final TransactionTemplate txTemplate;
    private final MemberRepositoryV3 memberRepositoryV3;

    public MemberServiceV3_1(PlatformTransactionManager tx, MemberRepositoryV3 memberRepositoryV3) {
        this.txTemplate = new TransactionTemplate(tx);
        this.memberRepositoryV3 = memberRepositoryV3;
    }

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
        txTemplate.executeWithoutResult(transactionStatus -> {
            try {
                businessLogic(fromId, toId, money);
            } catch (SQLException e) { //체크예외
                throw new IllegalStateException(e); //런타임 예외
            }
        });

    }

    private void businessLogic(String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepositoryV3.findById(fromId);
        Member toMember = memberRepositoryV3.findById(toId);

        memberRepositoryV3.update(fromId, fromMember.getMoney() - money);

        validation(toMember);
        memberRepositoryV3.update(toId, toMember.getMoney() + money);
    }

    private void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 예외 발생");
        }
    }
}
