package cn.harrison.sso.service;

import cn.harrison.sso.common.Result;
import cn.harrison.sso.entity.User;
import java.util.Map;

public interface AuthService {
    Result<Map<String, Object>> login(String username, String password);
    Result<Map<String, Object>> getUserInfo(String token);
}
