package cn.ly.base_common.support.misc.consts;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * Created by liaomengge on 2018/12/20.
 */
public final class ToolConst {

    public static final Splitter SPLITTER = Splitter.on(",").omitEmptyStrings().trimResults();
    public static final Joiner JOINER = Joiner.on(",").skipNulls();
}
