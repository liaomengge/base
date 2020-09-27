package cn.ly.base_common.support.misc;

import java.nio.charset.Charset;

/**
 * Created by liaomengge on 17/11/25.
 */
public interface Charsets {

    Charset UTF_8 = Charset.forName(Encodings.UTF_8);
    String UTF_8_NAME = UTF_8.name();

    Charset GBK = Charset.forName(Encodings.GBK);
    String GBK_NAME = GBK.name();

    Charset ISO_8859_1 = Charset.forName(Encodings.ISO_8859_1);
    String ISO_8859_1_NAME = ISO_8859_1.name();
}
