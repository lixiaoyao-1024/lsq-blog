package com.zmr.blogbackend.common;

import java.util.List;

public record PageResponse<T>(
        List<T> records,
        long total,
        long size,
        long current
) {
}
