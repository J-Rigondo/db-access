package com.jun.dbaccess.service;

import com.jun.dbaccess.domain.Member;
import com.jun.dbaccess.repository.MemberRepositoryV3;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@RequiredArgsConstructor
public class MemberServiceV3_3 {
    private final MemberRepositoryV3 memberRepositoryV3;

    @Transactional
    public void accountTransfer(String fromId, String toId, int money) throws SQLException {
                businessLogic(fromId, toId, money);

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
