package cn.harrison.sso.controller;

import cn.harrison.sso.common.Result;
import cn.harrison.sso.entity.Role;
import cn.harrison.sso.service.RoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    // 获取角色列表
    @GetMapping("/list")
    public Result<Page<Role>> getRoleList(@RequestParam(required = false, defaultValue = "1") Integer current,
                                          @RequestParam(required = false, defaultValue = "10") Integer size,
                                          Role role) {
        Page<Role> page = new Page<>(current, size);
        Page<Role> roleList = roleService.getRoleList(page, role);
        return Result.success(roleList);
    }

    // 新增角色
    @PostMapping("/add")
    public Result<?> addRole(@RequestBody Role role) {
        roleService.addRole(role);
        return Result.success();
    }

    // 修改角色
    @PutMapping("/update")
    public Result<?> updateRole(@RequestBody Role role) {
        roleService.updateRole(role);
        return Result.success();
    }

    // 删除角色
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }

    // 获取角色拥有的权限ID列表
    @GetMapping("/permissions/{roleId}")
    public Result<List<Long>> getRolePermissions(@PathVariable Long roleId) {
        List<Long> permissionIds = roleService.getRolePermissions(roleId);
        return Result.success(permissionIds);
    }

    // 分配权限
    @PostMapping("/assign-permissions")
    public Result<?> assignPermissions(@RequestBody AssignPermissionRequest request) {
        roleService.assignPermissions(request.getRoleId(), request.getPermissionIds());
        return Result.success();
    }

    @Data
    public static class AssignPermissionRequest {
        private Long roleId;
        private List<Long> permissionIds;
    }
}
