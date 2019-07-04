package com.hza.ssm.service.impl;

import com.hza.ssm.service.IDeptService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.stereotype.Service;

@Service
public class DeptServiceImpl implements IDeptService {

    @Override

    public void list() {
        System.out.println("==================(部门列表)====================");
    }
}
