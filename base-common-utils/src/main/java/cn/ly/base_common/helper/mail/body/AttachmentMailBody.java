package cn.ly.base_common.helper.mail.body;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.File;
import java.util.List;

/**
 * Created by liaomengge on 2019/8/28.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AttachmentMailBody extends AbstractMailBody {

    private String text;
    private List<AttachmentMailFile> attachmentMailFiles;

    @Data
    public static class AttachmentMailFile {
        private String attachmentFilename;
        private File file;
    }
}
