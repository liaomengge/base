package cn.ly.base_common.helper.mail.body;

import java.io.File;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by liaomengge on 2019/8/28.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class InlineMailBody extends AbstractMailBody {

    private String text;
    private List<InlineMailFile> inlineMailFiles;

    @Data
    public static class InlineMailFile {
        private String contentId;
        private File file;
    }
}
