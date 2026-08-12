package com.zmr.blogbackend.controller;

import com.zmr.blogbackend.config.AuthInterceptor;
import com.zmr.blogbackend.dto.LoginRequest;
import com.zmr.blogbackend.dto.LoginResult;
import com.zmr.blogbackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /** 登录：校验通过返回签名令牌 */
    @PostMapping("/login")
    public LoginResult login(@RequestBody LoginRequest request) {
        return authService.login(request.username(), request.password());
    }

    /** 退出：无状态令牌，客户端删除本地令牌即可，服务端无需任何操作 */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    /** 当前登录用户（需令牌，拦截器已注入用户名） */
    @GetMapping("/me")
    public Map<String, String> me(HttpServletRequest request) {
        Object username = request.getAttribute(AuthInterceptor.ATTR_USERNAME);
        return Map.of("username", username == null ? "" : String.valueOf(username));
    }
}
