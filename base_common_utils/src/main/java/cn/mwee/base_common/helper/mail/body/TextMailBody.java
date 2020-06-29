package cn.mwee.base_common.helper.mail.body;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by liaomengge on 2019/8/28.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TextMailBody extends AbstractMailBody {

    private String text;
}
