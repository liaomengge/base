package com.github.liaomengge.base_common.utils.io;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Created by liaomengge on 16/12/6.
 */
@Slf4j
@UtilityClass
public class LyIOUtil {

    public final byte[] toByteArray(InputStream inputStream) {
        try {
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.error("读取文件流失败", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭文件流失败", e);
                }
            }
        }

        return new byte[0];
    }

    public final byte[] toByteArray(InputStream inputStream, int size) {
        try {
            return IOUtils.toByteArray(inputStream, size);
        } catch (IOException e) {
            log.error("读取文件流失败", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭文件流失败", e);
                }
            }
        }

        return new byte[0];
    }

    public byte[] toByteArray(Reader input, Charset encoding) {
        try {
            return IOUtils.toByteArray(input, encoding);
        } catch (IOException e) {
            log.error("读取文件流失败", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error("关闭文件流失败", e);
                }
            }
        }

        return new byte[0];
    }

    public String toString(InputStream inputStream) {
        return toString(inputStream, Charset.defaultCharset());
    }

    public String toString(InputStream inputStream, Charset charset) {
        try {
            return IOUtils.toString(inputStream, charset);
        } catch (IOException e) {
            log.error("读取文件流失败", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("关闭文件流失败", e);
                }
            }
        }

        return "";
    }

    public String toString(Reader input) {
        try {
            return IOUtils.toString(input);
        } catch (IOException e) {
            log.error("读取文件流失败", e);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error("关闭文件流失败", e);
                }
            }
        }

        return "";
    }

    /**
     * Close closable object and wrap {@link IOException} with {@link
     * RuntimeException}
     *
     * @param closeable
     * @throws IOException
     */
    public void close(Closeable closeable) throws IOException {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                throw new IOException("IOException occurred. ", e);
            }
        }
    }


    /**
     * Close closable and hide possible {@link IOException}
     *
     * @param closeable closeable object
     */
    public void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        }
        if (closeable instanceof Flushable) {
            try {
                ((Flushable) closeable).flush();
            } catch (IOException ignored) {
            }
        }
        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }
}
