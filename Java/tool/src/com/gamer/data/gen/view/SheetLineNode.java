package com.gamer.data.gen.view;

import com.gamer.data.gen.Title;

/**
 * 复选框节点数据类行
 */
public class SheetLineNode extends SheetNode {

    public Title title;

    public SheetLineNode(Title title, boolean selected) {
        super(title.toString(), selected);
        this.title = title;
    }

    @Override
    public String toString() {
        return title.toString();
    }
}
