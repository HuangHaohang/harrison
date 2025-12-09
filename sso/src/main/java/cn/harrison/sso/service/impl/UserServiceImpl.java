package cn.harrison.sso.service.impl;

import cn.harrison.sso.entity.User;
import cn.harrison.sso.entity.UserRole;
import cn.harrison.sso.mapper.UserMapper;
import cn.harrison.sso.mapper.UserRoleMapper;
import cn.harrison.sso.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<User> getUserList(Page<User> page, User user) {
        return baseMapper.selectUserListWithRole(page, user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUser(User user) {
        // Set default password
        user.setPassword(passwordEncoder.encode("123456"));
        this.save(user);
        saveUserRoles(user.getUserId(), user.getRoleIds());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user) {
        // Don't update password here
        user.setPassword(null); 
        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        this.removeById(userId);
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = new User();
        user.setUserId(userId);
        user.setStatus(status);
        this.updateById(user);
    }

    @Override
    public void resetPassword(Long userId) {
        User user = new User();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode("123456"));
        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignUserRole(Long userId, Long roleId) {
        // Remove existing roles
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        
        // Add new role
        if (roleId != null) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRoleMapper.insert(userRole);
        }
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (!CollectionUtils.isEmpty(roleIds)) {
            for (Long roleId : roleIds) {
                UserRole userRole = new UserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoleMapper.insert(userRole);
            }
        }
    }
}
