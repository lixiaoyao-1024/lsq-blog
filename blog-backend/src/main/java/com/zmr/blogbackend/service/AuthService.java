package com.zmr.blogbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zmr.blogbackend.config.AuthTokenService;
import com.zmr.blogbackend.config.PasswordHasher;
import com.zmr.blogbackend.dto.LoginResult;
import com.zmr.blogbackend.entity.AdminUser;
import com.zmr.blogbackend.mapper.AdminUserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AdminUserMapper adminUserMapper;
    private final PasswordHasher passwordHasher;
    private final AuthTokenService authTokenService;

    @Value("${app.auth.token-ttl-hours:168}")
    private long tokenTtlHours;

    public AuthService(AdminUserMapper adminUserMapper,
                       PasswordHasher passwordHasher,
                       AuthTokenService authTokenService) {
        this.adminUserMapper = adminUserMapper;
        this.passwordHasher = passwordHasher;
        this.authTokenService = authTokenService;
    }

    /** 校验用户名 + 密码，成功签发令牌；失败抛 401 */
    public LoginResult login(String username, String rawPassword) {
        if (!StringUtils.hasText(username) || rawPassword == null || rawPassword.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }
        AdminUser user = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, username)
                .eq(AdminUser::getStatus, 1)
                .eq(AdminUser::getDeleted, 0));
        if (user == null || !passwordHasher.matches(rawPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }
        String token = authTokenService.issue(user.getUsername(), tokenTtlHours);
        return new LoginResult(token, user.getUsername(), user.getNickname());
    }
}
