package com.example.parking.util;

import com.example.parking.common.PageResult;
import java.util.List;

public final class PageUtils {

    private PageUtils() {
    }

    public static <T> PageResult<T> page(List<T> source, int pageNum, int pageSize) {
        int safePageNum = Math.max(pageNum, 1);
        int safePageSize = Math.max(pageSize, 1);
        int fromIndex = Math.min((safePageNum - 1) * safePageSize, source.size());
        int toIndex = Math.min(fromIndex + safePageSize, source.size());
        return new PageResult<>(source.size(), safePageNum, safePageSize, source.subList(fromIndex, toIndex));
    }
}
