package cn.ly.base_common.utils.zip;

import cn.ly.base_common.utils.log4j2.LyLogger;
import lombok.experimental.UtilityClass;
import org.apache.commons.compress.archivers.zip.Zip64Mode;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

/**
 * Zip格式文件 压缩、解压 工具类
 */
@UtilityClass
public class LyZipUtil {

    private Logger logger = LyLogger.getInstance(LyZipUtil.class);

    /**
     * 将文件压缩为 Zip 格式
     *
     * @param fileToZipList 待压缩的文件列表
     * @param targetPath    压缩后保存的路径 包括压缩文件名 如："D:/FTP/ziptest.zip"
     * @throws Exception
     */
    public void compress(List<File> fileToZipList, String targetPath)
            throws Exception {
        if (fileToZipList.size() == 0) {
            return;
        }
        // 没有文件名 则自动生成
        if (targetPath.endsWith("/")) {
            targetPath += getFormattedFileName();
        }
        // 文件名后缀是否为 .zip
        if (!targetPath.endsWith(".zip")) {
            targetPath += ".zip";
        }
        // 待压缩文件列表
        List<File> filesToArchive = fileToZipList;
        // 读取目标路径
        File zipFile = new File(targetPath);
        ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(zipFile);
        // Use Zip64 extensions for all entries where they are required
        zaos.setUseZip64(Zip64Mode.AsNeeded);
        logger.debug("Attempting to create " + targetPath + ".......");
        // 遍历 File List
        for (int i = 0; i < filesToArchive.size(); i++) {
            File file = filesToArchive.get(i);
            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(file,
                    file.getName());
            zaos.putArchiveEntry(zipArchiveEntry);
            InputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(file));
                byte[] buffer = new byte[1024 * 5];
                int len = -1;
                while ((len = is.read(buffer)) != -1) {
                    // 把缓冲区的字节写入到ZipArchiveEntry
                    zaos.write(buffer, 0, len);
                }
                // Writes all necessary data for this entry.
                zaos.closeArchiveEntry();
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        zaos.finish();
        zaos.close();
        logger.debug("Archive " + targetPath + " created successfully.......");

    }

    /**
     * 将 Zip 格式文件 解压
     *
     * @param sourcePath 待解压的文件路径 如："D:/FTP/Lodop6.zip"
     * @param targetPath 解压后保存的路径 如："D:/FTP/Lodop6/"
     * @throws Exception
     */
    public void uncompress(String sourcePath, String targetPath)
            throws Exception {
        // 路径为空直接返回
        if (StringUtils.isEmpty(sourcePath) || StringUtils.isEmpty(targetPath)) {
            return;
        }
        // 待解压文件是否存在
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            return;
        }
        // 目标路径是否存在、不存在 则新建目录
        File targetDirectory = new File(targetPath);
        if (!targetDirectory.exists()) {
            targetDirectory.mkdir();
        }
        // 解压方式由 ZipArchiveInputStream 修改为 ZipFile,否则会出现解压后的文件内容不完整
        // 解压输入流
        ZipFile zipFile = new ZipFile(sourceFile);
        //ZipArchiveInputStream zais = null;
        //zais = new ZipArchiveInputStream(new FileInputStream(sourceFile));
        ZipArchiveEntry archiveEntry = null;
        // 把zip包中的每个文件读取出来
        // 然后把文件写到指定的文件夹
        byte[] buf = new byte[65536];
        // 获取压缩包内的全部文件
        Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
        while (entries.hasMoreElements()) {
            archiveEntry = entries.nextElement();
            // while ((archiveEntry = (ZipArchiveEntry) zais.getNextEntry()) !=
            // null) {
            // 获取文件名
            String entryFileName = archiveEntry.getName();
            // 构造解压出来的文件存放路径
            String entryFilePath = targetPath + entryFileName;
            // byte[] content = new byte[(int) archiveEntry.getSize()];
            // zais.read(content,0,content.length);
            // OutputStream os = null;
            FileOutputStream fos = null;
            try {
                // 目标文件输出流
                fos = new FileOutputStream(entryFilePath);
                int n;
                InputStream entryContent = zipFile.getInputStream(archiveEntry);
                while ((n = entryContent.read(buf)) != -1) {
                    if (n > 0) {
                        fos.write(buf, 0, n);
                    }
                }
                // 把解压出来的文件写到指定路径
                // File entryFile = new File(entryFilePath);
                // os = new BufferedOutputStream(new
                // FileOutputStream(entryFile));
                // os.write(content);
            } finally {
                /*
                 * if (os != null) { os.flush(); os.close(); }
                 */
                if (fos != null) {
                    fos.close();
                }
            }
        }
    }

    // 私有方法 自动生成 压缩文件名
    private String getFormattedFileName() {
        long millis = System.currentTimeMillis();
        Date date = new Date(millis);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        String filename = "Archive_" + df.format(date) + ".zip";
        return filename;
    }
}
