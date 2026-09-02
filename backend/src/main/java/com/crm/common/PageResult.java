package com.crm.common;

import lombok.Data;

import java.util.List;

/**
 * 分页响应体。
 */
@Data
public class PageResult<T> {

    private List<T> records;
    private long total;

    public PageResult() {
    }

    public PageResult(List<T> records, long total) {
        this.records = records;
        this.total = total;
    }
}
