package com.gamer.data.excel;

/**
 * 同步标志
 */
public class SyncLabel {
    // 滚动同步标志，防止循环同步
    public boolean isSyncingScroll = false;
    // 分页同步标志，防止循环同步
    public boolean isSyncingPagination = false;
}
