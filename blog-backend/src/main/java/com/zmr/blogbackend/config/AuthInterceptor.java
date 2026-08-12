package com.zmr.blogbackend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * 写操作鉴权拦截器。
 *
 * <p>规则（仅拦截 /api/**）：</p>
 * <ul>
 *   <li>{@code POST /api/auth/login}：放行</li>
 *   <li>其它 {@code /api/auth/**}（/me、/logout）：需令牌</li>
 *   <li>GET 且路径不含 {@code /admin}：公开浏览，放行（游客可浏览）</li>
 *   <li>其余所有写操作（POST/PUT/DELETE）与 admin 读接口：需有效令牌，否则 401</li>
 * </ul>
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String ATTR_USERNAME = "authUsername";

    private final AuthTokenService authTokenService;

    public AuthInterceptor(AuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String path = request.getRequestURI();

        if ("/api/auth/login".equals(path)) {
            return true;
        }
        if (path.startsWith("/api/auth/")) {
            return requireToken(request, response);
        }

        // 游客浏览：仅放行公开 GET（管理端读取 /admin/** 仍需登录）
        if ("GET".equals(request.getMethod()) && !path.contains("/admin")) {
            return true;
        }

        return requireToken(request, response);
    }

    private boolean requireToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String header = request.getHeader("Authorization");
        String token = header != null && header.startsWith("Bearer ") ? header.substring(7) : null;
        String username = authTokenService.verify(token);
        if (username == null) {
            writeUnauthorized(response);
            return false;
        }
        request.setAttribute(ATTR_USERNAME, username);
        return true;
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"未登录或登录已过期\"}");
    }
}
