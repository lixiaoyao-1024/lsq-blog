package com.zmr.blogbackend.config;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Argon2id 密码哈希器。
 *
 * <p>使用 argon2-jvm，输出为标准编码串（形如 {@code $argon2id$v=19$m=...$salt$hash}），
 * 盐值内嵌于编码串中，verify 时无需单独保存盐。参数参考 OWASP 建议的 Argon2id 强度。</p>
 */
@Component
public class PasswordHasher {

    private static final int MEMORY_KIB = 65_536; // 64 MB
    private static final int ITERATIONS = 3;
    private static final int PARALLELISM = 4;

    private final Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);

    /** 生成 Argon2id 哈希（内含随机盐，输出 32 字节） */
    public String hash(String rawPassword) {
        char[] chars = rawPassword == null ? new char[0] : rawPassword.toCharArray();
        try {
            return argon2.hash(ITERATIONS, MEMORY_KIB, PARALLELISM, chars, StandardCharsets.UTF_8);
        } finally {
            argon2.wipeArray(chars);
        }
    }

    /** 校验明文密码是否与存储的哈希匹配 */
    public boolean matches(String rawPassword, String encodedHash) {
        if (rawPassword == null || encodedHash == null) {
            return false;
        }
        char[] chars = rawPassword.toCharArray();
        try {
            return argon2.verify(encodedHash, chars, StandardCharsets.UTF_8);
        } finally {
            argon2.wipeArray(chars);
        }
    }
}
