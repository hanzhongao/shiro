package com.hza.ssm.service.impl;

import com.hza.ssm.dao.IMemberDAO;
import com.hza.ssm.service.IMemberService;
import com.hza.ssm.vo.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements IMemberService {
    @Autowired
    private IMemberDAO memberDAO ;
    @Override
    public Member get(String mid) {
        return this.memberDAO.findByID(mid);
    }
}
