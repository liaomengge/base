package cn.ly.base_common.utils.zip;

import cn.ly.base_common.utils.log4j2.LyLogger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import lombok.experimental.UtilityClass;

/**
 * 7z格式文件 压缩、解压 工具类
 */
@UtilityClass
public class Ly7ZUtil {

    private Logger log = LyLogger.getInstance(Ly7ZUtil.class);

    /**
     * 将文件压缩为 7z 格式
     *
     * @param fileToSevenZList 待压缩的文件列表
     * @param targetPath       压缩后保存的路径 包括压缩文件名 如："D:/FTP/Archive_20150115.7z"
     * @throws Exception
     */
    public void compress(List<File> fileToSevenZList, String targetPath) throws Exception {
        if (fileToSevenZList.size() == 0) {
            return;
        }
        // 没有文件名 则自动生成
        if (targetPath.endsWith("/")) {
            targetPath += getFormattedFileName();
        }
        // 文件名后缀是否为 .7z
        if (!targetPath.endsWith(".7z")) {
            targetPath += ".7z";
        }
        // 待压缩文件列表
        List<File> filesToArchive = fileToSevenZList;
        // 读取目标路径
        File sevenZFile = new File(targetPath);
        SevenZOutputFile sevenZOutput = null;
        log.debug("Attempting to create " + targetPath + ".......");

        sevenZOutput = new SevenZOutputFile(sevenZFile);
        // 遍历 File List 逐个压缩
        for (int i = 0; i < filesToArchive.size(); i++) {
            SevenZArchiveEntry entry = sevenZOutput.createArchiveEntry(
                    filesToArchive.get(i), filesToArchive.get(i).getName());
            sevenZOutput.putArchiveEntry(entry);
            FileInputStream in = new FileInputStream(filesToArchive.get(i));
            byte[] b = new byte[1024 * 5];
            int count = 0;
            while ((count = in.read(b)) > 0) {
                sevenZOutput.write(b, 0, count);
            }
            sevenZOutput.closeArchiveEntry();
            in.close();
        }
        sevenZOutput.close();
        sevenZFile = null;
        log.debug("Archive " + targetPath + " created successfully.......");

    }

    /**
     * 将 7z 格式文件 解压
     *
     * @param sourcePath 待解压的文件路径 如："D:/FTP/Archive_20150115.7z"
     * @param targetPath 解压后保存的路径 如："D:/FTP/20150115/"
     * @throws Exception
     */
    public void uncompress(String sourcePath, String targetPath) throws Exception {
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
        // 文件读取为 SevenZFile
        SevenZFile sevenZFile = new SevenZFile(sourceFile);
        SevenZArchiveEntry archiveEntry = null;
        // 然后把文件写到指定的文件夹
        while ((archiveEntry = sevenZFile.getNextEntry()) != null) {
            // 获取文件名
            String entryFileName = archiveEntry.getName();
            // 构造解压出来的文件存放路径
            String entryFilePath = targetDirectory + "/" + entryFileName;
            // 流输出
            byte[] content = new byte[(int) archiveEntry.getSize()];
            sevenZFile.read(content, 0, content.length);
            OutputStream os = null;
            try {
                // 把解压出来的文件写到指定路径
                File entryFile = new File(entryFilePath);
                os = new BufferedOutputStream(new FileOutputStream(entryFile));
                os.write(content);
            } finally {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            }
        }
        sevenZFile.close();
    }

    // 私有方法 自动生成 压缩文件名
    private String getFormattedFileName() {
        long millis = System.currentTimeMillis();
        Date date = new Date(millis);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        String filename = "Archive_" + df.format(date) + ".7z";
        return filename;
    }
}
