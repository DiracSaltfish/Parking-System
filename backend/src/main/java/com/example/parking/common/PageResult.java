package com.example.parking.common;

import java.util.List;

public record PageResult<T>(long total, int pageNum, int pageSize, List<T> records) {
}
