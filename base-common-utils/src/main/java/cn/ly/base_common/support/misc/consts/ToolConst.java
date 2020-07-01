package cn.ly.base_common.support.misc.consts;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * Created by liaomengge on 2018/12/20.
 */
public interface ToolConst {

    Splitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();
    Joiner JOINER = Joiner.on(",").skipNulls();
}
