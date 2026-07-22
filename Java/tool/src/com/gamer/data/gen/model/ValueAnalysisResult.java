package com.gamer.data.gen.model;

/**
 * 单个值分析结果
 */
public class ValueAnalysisResult {
    final boolean isTwoDimensional;
    final boolean isNumeric;
    final String[] subValues;

    public ValueAnalysisResult(boolean isTwoDimensional, boolean isNumeric, String[] subValues) {
        this.isTwoDimensional = isTwoDimensional;
        this.isNumeric = isNumeric;
        this.subValues = subValues;
    }
}