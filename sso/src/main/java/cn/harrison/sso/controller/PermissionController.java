package cn.harrison.sso.controller;

import cn.harrison.sso.common.Result;
import cn.harrison.sso.entity.Permission;
import cn.harrison.sso.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    // 获取权限列表（树形）
    @GetMapping("/list")
    public Result<List<Permission>> list(Permission permission) {
        List<Permission> list = permissionService.getPermissionTree(permission);
        return Result.success(list);
    }

    // 新增权限
    @PostMapping("/add")
    public Result<?> add(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.success();
    }

    // 修改权限
    @PutMapping("/update")
    public Result<?> update(@RequestBody Permission permission) {
        permissionService.updateById(permission);
        return Result.success();
    }

    // 删除权限
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        permissionService.removeById(id);
        return Result.success();
    }
}
