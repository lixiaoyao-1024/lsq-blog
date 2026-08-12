package com.zmr.blogbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 无状态签名令牌：{@code <base64url(username)>.<base64url(expirySeconds)>.<signature>}。
 *
 * <p>签名用 HMAC-SHA256，密钥来自 {@code app.auth.token-secret}；令牌含过期时间，
 * 重启服务不失效。无需服务端会话存储。</p>
 */
@Component
public class AuthTokenService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final byte[] secretBytes;

    public AuthTokenService(@Value("${app.auth.token-secret}") String secret) {
        this.secretBytes = secret.getBytes(StandardCharsets.UTF_8);
    }

    /** 为指定用户签发令牌，有效期 app.auth.token-ttl-hours（默认 168 小时 = 7 天） */
    public String issue(String username, long ttlHours) {
        long expiry = System.currentTimeMillis() / 1000 + ttlHours * 3600;
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(username.getBytes(StandardCharsets.UTF_8))
                + "." + expiry;
        return payload + "." + sign(payload);
    }

    /**
     * 校验令牌有效性。
     *
     * @return 有效返回用户名，无效（签名错误 / 过期）返回 null
     */
    public String verify(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String[] parts = token.split("\\.", -1);
        if (parts.length != 3) {
            return null;
        }
        String payload = parts[0] + "." + parts[1];
        if (!constantTimeEquals(sign(payload), parts[2])) {
            return null;
        }
        long expiry;
        try {
            expiry = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            return null;
        }
        if (System.currentTimeMillis() / 1000 >= expiry) {
            return null;
        }
        byte[] decoded;
        try {
            decoded = Base64.getUrlDecoder().decode(parts[0]);
        } catch (IllegalArgumentException e) {
            return null;
        }
        return new String(decoded, StandardCharsets.UTF_8);
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secretBytes, HMAC_ALGORITHM));
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to sign auth token", e);
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8));
    }
}
