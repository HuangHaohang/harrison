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
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PermissionMapper permissionMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // Mock storage for verification codes: Mobile -> Code
    private static final Map<String, String> MOCK_CODE_STORE = new ConcurrentHashMap<>();

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
        data.put("username", user.getUsername());
        data.put("avatar", user.getAvatar());
        data.put("menus", menuTree);
        data.put("permissions", permissions.stream().map(Permission::getCode).collect(Collectors.toList()));
        
        return Result.success(data);
    }

    @Override
    public void register(User user) {
        if (StringUtils.hasText(user.getUsername())) {
            long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, user.getUsername()));
            if (count > 0) {
                throw new RuntimeException("Username already exists");
            }
        }
        
        if (StringUtils.hasText(user.getMobile())) {
             long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getMobile, user.getMobile()));
             if (count > 0) {
                 throw new RuntimeException("Mobile number already registered");
             }
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1); // Enabled by default
        userMapper.insert(user);
    }

    @Override
    public void sendResetCode(String mobile) {
        if (!StringUtils.hasText(mobile)) {
            throw new RuntimeException("Mobile number cannot be empty");
        }
        
        long count = userMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getMobile, mobile));
        if (count == 0) {
            throw new RuntimeException("Mobile number not found");
        }

        // Generate 6 digit code
        String code = String.valueOf((int)((Math.random() * 9 + 1) * 100000));
        MOCK_CODE_STORE.put(mobile, code);
        
        // Mock sending: Print to console
        System.out.println("==========================================");
        System.out.println("Mock SMS Sending to: " + mobile);
        System.out.println("Verification Code: " + code);
        System.out.println("==========================================");
    }

    @Override
    public void resetPasswordByMobile(String mobile, String code, String newPassword) {
        String storedCode = MOCK_CODE_STORE.get(mobile);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new RuntimeException("Invalid verification code");
        }
        
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getMobile, mobile));
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        
        // Clear code
        MOCK_CODE_STORE.remove(mobile);
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
