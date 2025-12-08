package cn.harrison.sso.service;

import cn.harrison.sso.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PermissionService extends IService<Permission> {
    List<Permission> getPermissionTree(Permission permission);
}
