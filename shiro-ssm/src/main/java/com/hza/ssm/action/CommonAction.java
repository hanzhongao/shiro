package com.hza.ssm.action;

import com.hza.ssm.service.IDeptService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CommonAction {
    @Autowired
    private IDeptService deptService ;

    @RequestMapping("/login")
    public String login() {
        return "login" ;
    }

//    @RequiresRoles("member")
//    @RequiresPermissions("dept:add")
    @RequestMapping("/pages/welcome")
    public String welcome() {
        this.deptService.list();
        return "welcome" ;
    }

    @RequestMapping("/logoutInfo")
    public String logout() {
        return "logoutInfo" ;
    }

    @RequestMapping("/noauthz")
    public String noauthz() {
        return "plugins/noauthz" ;
    }

}
