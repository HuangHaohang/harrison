package cn.harrison.sso.controller;

import cn.harrison.sso.common.Result;
import cn.harrison.sso.entity.User;
import cn.harrison.sso.service.AuthService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return authService.login(request.getUsername(), request.getPassword());
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        authService.register(user);
        return Result.success();
    }

    @PostMapping("/send-code")
    public Result<?> sendCode(@RequestBody Map<String, String> params) {
        String mobile = params.get("mobile");
        authService.sendResetCode(mobile);
        return Result.success();
    }

    @PostMapping("/reset-password")
    public Result<?> resetPassword(@RequestBody Map<String, String> params) {
        String mobile = params.get("mobile");
        String code = params.get("code");
        String password = params.get("password");
        authService.resetPasswordByMobile(mobile, code, password);
        return Result.success();
    }

    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return authService.getUserInfo(token);
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
