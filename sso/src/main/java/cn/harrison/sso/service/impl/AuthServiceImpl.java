package cn.harrison.sso.service.impl;

import cn.harrison.sso.common.Result;
import cn.harrison.sso.entity.Permission;
import cn.harrison.sso.entity.User;
import cn.harrison.sso.mapper.PermissionMapper;
import cn.harrison.sso.mapper.UserMapper;
import cn.harrison.sso.service.AuthService;
import cn.harrison.sso.utils.JwtUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PermissionMapper permissionMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public Result<Map<String, Object>> login(String username, String password) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return Result.error(401, "User not found");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Result.error(401, "Invalid password");
        }
        
        if (user.getStatus() != null && user.getStatus() == 0) {
            return Result.error(403, "Account disabled");
        }

        String token = jwtUtils.generateToken(user.getUserId(), user.getUsername());
        Map<String, Object> map = new HashMap<>();
        map.put("token", token);
        return Result.success(map);
    }

    @Override
    public Result<Map<String, Object>> getUserInfo(String token) {
        if (!jwtUtils.validateToken(token)) {
            return Result.error(401, "Invalid token");
        }
        Long userId = jwtUtils.getUserIdFromToken(token);
        User user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error(401, "User not found");
        }

        List<Permission> permissions = permissionMapper.selectPermissionsByUserId(userId);
        
        // Build menu tree
        List<Permission> menuTree = buildMenuTree(permissions);
        
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("menus", menuTree);
        data.put("permissions", permissions.stream().map(Permission::getCode).collect(Collectors.toList()));
        
        return Result.success(data);
    }

    private List<Permission> buildMenuTree(List<Permission> permissions) {
        List<Permission> trees = new ArrayList<>();
        for (Permission permission : permissions) {
            if (permission.getParentId() == 0) {
                permission.setChildren(getChildren(permission.getId(), permissions));
                trees.add(permission);
            }
        }
        return trees;
    }

    private List<Permission> getChildren(Long parentId, List<Permission> permissions) {
        List<Permission> children = new ArrayList<>();
        for (Permission permission : permissions) {
            if (permission.getParentId().equals(parentId)) {
                permission.setChildren(getChildren(permission.getId(), permissions));
                children.add(permission);
            }
        }
        return children;
    }
}
