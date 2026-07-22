package com.gamer.data.excel.view;

import com.gamer.data.excel.BaseCheck;

import javax.swing.*;

/**
 * 数据构建检查
 *
 * @author liuyunhui
 * @date 2025/12/05
 */
public class DiffCheck extends BaseCheck {
    public DiffCheck() {}

    @Override
    protected JFrame createViewer() {
        return new ExcelGDDiffView(this);
    }

}