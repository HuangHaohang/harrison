package cn.harrison.sso.controller;

import cn.harrison.sso.common.Result;
import cn.harrison.sso.entity.User;
import cn.harrison.sso.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("sys/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 获取用户列表
    @GetMapping("/list")
    public Result<Page<User>> getUserList(@RequestParam(required = false, defaultValue = "1") Integer current,
                                          @RequestParam(required = false, defaultValue = "10") Integer size,
                                          User user) {
        Page<User> page = new Page<>(current, size);
        Page<User> userList = userService.getUserList(page, user);
        return Result.success(userList);
    }

    // 新增用户
    @PostMapping("/add")
    public Result<?> addUser(@RequestBody User user) {
        userService.addUser(user);
        return Result.success();
    }

    // 修改用户
    @PutMapping("/update")
    public Result<?> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success();
    }

    // 删除用户
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    // 修改用户状态
    @PutMapping("/status/{id}")
    public Result<?> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Integer> params) {
        Integer status = params.get("status");
        userService.updateUserStatus(id, status);
        return Result.success();
    }

    // 重置密码
    @PutMapping("/reset-password/{id}")
    public Result<?> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        return Result.success();
    }

    // 分配角色
    @PostMapping("/assign-role")
    public Result<?> assignUserRole(@RequestBody Map<String, Long> params) {
        Long userId = params.get("userId");
        Long roleId = params.get("roleId");
        userService.assignUserRole(userId, roleId);
        return Result.success();
    }
}
