package com.hza.ssm.realm;

import com.hza.ssm.service.IMemberPrivilegeService;
import com.hza.ssm.service.IMemberService;
import com.hza.ssm.vo.Member;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import java.util.Set;

public class MemberRealm extends AuthorizingRealm {

    @Autowired
    private IMemberService memberService ;

    @Autowired
    private IMemberPrivilegeService memberPrivilegeService ;

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("【1】========================用户认证========================");
        String mid = (String) token.getPrincipal();
        Member member = this.memberService.get(mid) ;
        if (member == null) {
            throw new UnknownAccountException(mid + "用户不存在") ;
        }
//        String password = new String((char[]) token.getCredentials());
//        if (!member.getPassword().equals(password)) {
//            throw new IncorrectCredentialsException("用户名或密码错误") ;
//        }
        if (member.getLocked().equals(1)) {
            throw new LockedAccountException(mid + "用户已被锁定") ;
        }
        return new SimpleAuthenticationInfo(token.getPrincipal(),member.getPassword(),this.getName());
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("【2】++++++++++++++++++++++++++++++++用户授权++++++++++++++++++++++++++++++++");
        Map<String, Set<String>> map =
                this.memberPrivilegeService.getByMember((String) principalCollection.getPrimaryPrincipal()) ;
        SimpleAuthorizationInfo authz = new SimpleAuthorizationInfo();
        authz.setRoles(map.get("allRoles"));
        authz.setStringPermissions(map.get("allActions"));
        return authz;
    }
}
