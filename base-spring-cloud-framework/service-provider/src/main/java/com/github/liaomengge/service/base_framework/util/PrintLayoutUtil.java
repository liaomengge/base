package com.github.liaomengge.service.base_framework.util;

import com.taobao.text.ui.Overflow;
import com.taobao.text.ui.TableElement;
import com.taobao.text.util.RenderUtil;
import lombok.experimental.UtilityClass;

import static com.taobao.text.ui.Element.row;

/**
 * Created by liaomengge on 2020/10/17.
 */
@UtilityClass
public class PrintLayoutUtil {

    public TableElement buildTableStyle() {
        TableElement tableElement = new TableElement();
        tableElement.leftCellPadding(0).rightCellPadding(1);

        // Overflow.HIDDEN 表示隐藏
        // Overflow.WRAP表示会向外面排出去，即当输出宽度有限时，右边的列可能会显示不出
        tableElement.overflow(Overflow.HIDDEN);
        return tableElement;
    }

    public void addRowElement(TableElement tableElement, String... text) {
        tableElement.add(row().add((text)));
    }

    public String render(TableElement tableElement) {
        return RenderUtil.render(tableElement);
    }
}
