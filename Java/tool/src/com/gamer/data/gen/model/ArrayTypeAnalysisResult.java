package com.gamer.data.gen.model;

/**
 * 数组类型分析结果
 */
public class ArrayTypeAnalysisResult {
    final boolean hasTwoDimensionalStructure;
    final boolean allInt;

    public ArrayTypeAnalysisResult(boolean hasTwoDimensionalStructure, boolean allInt) {
        this.hasTwoDimensionalStructure = hasTwoDimensionalStructure;
        this.allInt = allInt;
    }
}