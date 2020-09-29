package cn.ly.base_common.utils.file;

import cn.ly.base_common.support.misc.enums.OSTypeEnum;
import cn.ly.base_common.utils.os.LyOSUtil;
import cn.ly.base_common.utils.regex.LyMatcherUtil;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.io.File;


/**
 * 文件工具类
 */
@UtilityClass
public class LyFileUtil {

    /**
     * 判断文件是否存在
     *
     * @param fileName
     * @return
     */
    public boolean exist(String fileName) {
        File f = new File(fileName);
        return f.exists();
    }

    /**
     * 取得jar文件的执行路径
     *
     * @param clz jar包里的main-class
     * @return
     */
    public String getJarExecPath(Class clz) {
        String path = clz.getProtectionDomain().getCodeSource().getLocation().getPath();
        if (LyOSUtil.getOSname().equals(OSTypeEnum.Windows)) {
            return path.substring(1);
        }
        return path;
    }

    /**
     * 判断文件路径是否绝对路径
     *
     * @param filePath
     * @return
     */
    public boolean isAbsolutePath(String filePath) {
        String temp = StringUtils.replace(filePath, "\\", "/");
        return temp.startsWith("/") || LyMatcherUtil.isPartMatch(temp, "^[A-Za-z]:/");

    }
}
