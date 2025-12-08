package cn.harrison.sso.service;

import cn.harrison.sso.entity.User;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    Page<User> getUserList(Page<User> page, User user);
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(Long userId);
    void updateUserStatus(Long userId, Integer status);
    void resetPassword(Long userId);
}
