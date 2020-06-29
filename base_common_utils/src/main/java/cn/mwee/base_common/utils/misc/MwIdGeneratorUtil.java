package cn.mwee.base_common.utils.misc;

import cn.mwee.base_common.support.misc.micro.Snowflake;
import cn.mwee.base_common.utils.number.MwNumberUtil;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by liaomengge on 17/11/25.
 */
@UtilityClass
public class MwIdGeneratorUtil {

    /**
     * 获取36位UUID(原生UUID)
     * 封装JDK自带的UUID, 通过Random数字生成, 中间有-分割.
     */
    public String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取32位UUID
     * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
     */
    public String uuid2() {
        return StringUtils.replaceChars(UUID.randomUUID().toString(), "-", "");
    }

    /**
     * 获取36位UUID(原生UUID)
     * 快速生成uuid, 中间有-分隔
     *
     * @return
     */
    public String fastUuid() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        UUID uuid = new UUID(random.nextLong(), random.nextLong());
        return uuid.toString();
    }

    /**
     * 获取32位UUID
     * 快速生成uuid, 中间无-分隔
     *
     * @return
     */
    public String fastUuid2() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        UUID uuid = new UUID(random.nextLong(), random.nextLong());
        return StringUtils.replaceChars(uuid.toString(), "-", "");
    }

    /**
     * snowflake id生产
     *
     * @return
     */
    public Long snowflakeId() {
        return new Snowflake().nextId();
    }

    /**
     * snowflake id生产
     *
     * @return
     */
    public String snowflakeId2Str() {
        return MwNumberUtil.getString(new Snowflake().nextId());
    }
}
