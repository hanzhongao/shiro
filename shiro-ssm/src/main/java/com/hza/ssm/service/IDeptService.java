package com.hza.ssm.service;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;

public interface IDeptService {
    @RequiresRoles("member")
    @RequiresPermissions("dept:add")
    public void list() ;
}
