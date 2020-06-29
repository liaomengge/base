package cn.mwee.base_common.utils.os;

import cn.mwee.base_common.support.misc.enums.OSTypeEnum;
import lombok.experimental.UtilityClass;

/**
 * 操作系统工具类(用于判断当前机器的OS类型)
 */
@UtilityClass
public class MwOSUtil {

    private String OS = System.getProperty("os.name").toLowerCase();

    private MwOSUtil instance = new MwOSUtil();

    private OSTypeEnum platform;

    public boolean isLinux() {
        return OS.indexOf("linux") >= 0;
    }

    public boolean isMacOS() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") < 0;
    }

    public boolean isMacOSX() {
        return OS.indexOf("mac") >= 0 && OS.indexOf("os") > 0 && OS.indexOf("x") > 0;
    }

    public boolean isWindows() {
        return OS.indexOf("windows") >= 0;
    }

    public boolean isOS2() {
        return OS.indexOf("os/2") >= 0;
    }

    public boolean isSolaris() {
        return OS.indexOf("solaris") >= 0;
    }

    public boolean isSunOS() {
        return OS.indexOf("sunos") >= 0;
    }

    public boolean isMPEiX() {
        return OS.indexOf("mpe/ix") >= 0;
    }

    public boolean isHPUX() {
        return OS.indexOf("hp-ux") >= 0;
    }

    public boolean isAix() {
        return OS.indexOf("aix") >= 0;
    }

    public boolean isOS390() {
        return OS.indexOf("os/390") >= 0;
    }

    public boolean isFreeBSD() {
        return OS.indexOf("freebsd") >= 0;
    }

    public boolean isIrix() {
        return OS.indexOf("irix") >= 0;
    }

    public boolean isDigitalUnix() {
        return OS.indexOf("digital") >= 0 && OS.indexOf("unix") > 0;
    }

    public boolean isNetWare() {
        return OS.indexOf("netware") >= 0;
    }

    public boolean isOSF1() {
        return OS.indexOf("osf1") >= 0;
    }

    public boolean isOpenVMS() {
        return OS.indexOf("openvms") >= 0;
    }

    /**
     * 获取操作系统名字
     *
     * @return 操作系统名
     */
    public OSTypeEnum getOSname() {
        if (isAix()) instance.platform = OSTypeEnum.AIX;
        else if (isDigitalUnix()) instance.platform = OSTypeEnum.Digital_Unix;
        else if (isFreeBSD()) instance.platform = OSTypeEnum.FreeBSD;
        else if (isHPUX()) instance.platform = OSTypeEnum.HP_UX;
        else if (isIrix()) instance.platform = OSTypeEnum.Irix;
        else if (isLinux()) instance.platform = OSTypeEnum.Linux;
        else if (isMacOS()) instance.platform = OSTypeEnum.Mac_OS;
        else if (isMacOSX()) instance.platform = OSTypeEnum.Mac_OS_X;
        else if (isMPEiX()) instance.platform = OSTypeEnum.MPEiX;
        else if (isNetWare()) instance.platform = OSTypeEnum.NetWare_411;
        else if (isOpenVMS()) instance.platform = OSTypeEnum.OpenVMS;
        else if (isOS2()) instance.platform = OSTypeEnum.OS2;
        else if (isOS390()) instance.platform = OSTypeEnum.OS390;
        else if (isOSF1()) instance.platform = OSTypeEnum.OSF1;
        else if (isSolaris()) instance.platform = OSTypeEnum.Solaris;
        else if (isSunOS()) instance.platform = OSTypeEnum.SunOS;
        else if (isWindows()) instance.platform = OSTypeEnum.Windows;
        else instance.platform = OSTypeEnum.Others;
        return instance.platform;
    }
}
