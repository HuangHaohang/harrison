package cn.harrison.sso.service;

import cn.harrison.sso.entity.Role;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RoleService extends IService<Role> {
    Page<Role> getRoleList(Page<Role> page, Role role);
    void addRole(Role role);
    void updateRole(Role role);
    void deleteRole(Long roleId);
    List<Long> getRolePermissions(Long roleId);
    void assignPermissions(Long roleId, List<Long> permissionIds);
}
