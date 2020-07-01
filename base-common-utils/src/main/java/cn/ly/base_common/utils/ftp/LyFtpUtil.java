package cn.ly.base_common.utils.ftp;

import cn.ly.base_common.utils.log4j2.LyLogger;
import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPConnector;
import it.sauronsoftware.ftp4j.FTPDataTransferListener;
import it.sauronsoftware.ftp4j.FTPFile;
import it.sauronsoftware.ftp4j.connectors.DirectConnector;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.InputStream;

/**
 * Created by liaomengge on 17/10/11.
 */
@UtilityClass
public class LyFtpUtil {

    private Logger logger = LyLogger.getInstance(LyFtpUtil.class);

    /**
     * 将流上传到指定远程服务器
     *
     * @param serverIP
     * @param serverPort
     * @param ftpUser
     * @param ftpPwd
     * @param fileName
     * @param inputStream
     * @param remoteFilePath
     * @throws Exception
     */
    public void upload(String serverIP, int serverPort, String ftpUser,
                       String ftpPwd, String fileName, InputStream inputStream, String remoteFilePath) throws Exception {

        logger.info("start uploading file[{}] to ftp...", fileName);
        FTPClient client = null;
        try {
            client = getFtpClient(serverIP, serverPort, ftpUser, ftpPwd);
            upload(client, fileName, inputStream, remoteFilePath);
        } finally {
            closeClient(client);
        }
        logger.info("complete uploading file[{}] to ftp...", fileName);
    }

    public void upload(FTPClient client, String fileName, InputStream inputStream, String remoteFilePath) throws Exception {
        // 获得 FTP client
        if (client == null) return;

        remoteFilePath = StringUtils.replace(remoteFilePath, "\\", "/");
        String remoteDirPath = remoteFilePath.substring(0, remoteFilePath.lastIndexOf('/'));

        // 判断远程路径是否存在
        int isExists = isExist(client, remoteDirPath);

        // 不存在创建目录
        if (isExists < 0) client.createDirectory(remoteDirPath);
        // 切换到 目标路径
        client.changeDirectory(remoteDirPath);

        //判断远程文件是否存在
        isExists = isExist(client, remoteFilePath + fileName);

        //已经存在, 先删除
        if (isExists == 0) delete(client, remoteFilePath + fileName);

        if (!client.currentDirectory().equalsIgnoreCase(remoteDirPath)) client.changeDirectory(remoteDirPath);

        // 上传文件
        client.upload(fileName, inputStream, 0L, 0L, new CustomTransferListener(fileName));

        // 上传完成 切回 根目录
        client.changeDirectory("/");

    }

    /**
     * FTP 删除远程文件
     *
     * @param client
     * @param remoteFilePath
     * @throws Exception
     */
    public void delete(FTPClient client, String remoteFilePath) throws Exception {

        if (StringUtils.isEmpty(remoteFilePath)) return;

        if (client == null) return;

        // 判断远程路径是否存在
        int isExists = isExist(client, remoteFilePath);

        // 删除文件
        if (isExists == FTPFile.TYPE_FILE) client.deleteFile(remoteFilePath);
        // 删除文件夹
        if (isExists == FTPFile.TYPE_DIRECTORY) deleteFolder(client, remoteFilePath);
        // 删除完成 切回 根目录
        client.changeDirectory("/");

    }

    /**
     * FTP 删除文件夹
     * 私有方法
     *
     * @param client   FTP Client
     * @param filePath 文件夹路径
     */
    private void deleteFolder(FTPClient client, String filePath)
            throws Exception {
        String path = filePath;
        client.changeDirectory(path);
        FTPFile[] files = client.list();
        String name = null;

        for (FTPFile file : files) {
            name = file.getName();
            // 排除隐藏目录
            if (".".equals(name) || "..".equals(name)) continue;
            // 递归删除子目录
            if (file.getType() == FTPFile.TYPE_DIRECTORY)
                deleteFolder(client, client.currentDirectory() + File.separator
                        + file.getName());
            else // 删除文件
                if (file.getType() == FTPFile.TYPE_FILE) client.deleteFile(file.getName());
        }
        client.changeDirectoryUp();// 反回上一级目录
        client.deleteDirectory(filePath); // 删除当前目录
    }

    /**
     * 判断一个FTP路径是否存在, 如果存在返回类型(FTPFile.TYPE_DIRECTORY=1、FTPFile.TYPE_FILE=0、
     * FTPFile.TYPE_LINK=2) 如果文件不存在, 则返回一个-1
     *
     * @param client         FTP客户端
     * @param remoteFilePath FTP文件或文件夹路径
     * @return 存在时候返回类型值(文件0, 文件夹1, 连接2), 不存在则返回-1
     */
    public int isExist(FTPClient client, String remoteFilePath) {
        int x = -1;
        if (StringUtils.isEmpty(remoteFilePath)) return x;
        FTPFile[] list = null;
        try {
            list = client.list(remoteFilePath);
        } catch (Exception e) {
            return -1;
        }
        if (list.length > 1) return FTPFile.TYPE_DIRECTORY;
        else if (list.length == 1) {
            FTPFile f = list[0];
            if (f.getType() == FTPFile.TYPE_DIRECTORY) return FTPFile.TYPE_DIRECTORY;

            // 假设推理判断
            String path = remoteFilePath + "/" + f.getName();
            try {
                int y = client.list(path).length;
                if (y == 1) return FTPFile.TYPE_DIRECTORY;
                else return FTPFile.TYPE_FILE;
            } catch (Exception e) {
                return FTPFile.TYPE_FILE;
            }
        } else try {
            client.changeDirectory(remoteFilePath);
            return FTPFile.TYPE_DIRECTORY;
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * 关闭Ftp连接
     *
     * @param client
     */
    public void closeClient(FTPClient client) {
        if (client != null) {
            if (client.isConnected()) try {
                client.logout();
                client.disconnect(true);
            } catch (Exception e) {
                logger.info("close Ftp Connection error!", e);
            }
            client = null;
        }
    }

    // FTP 传输 监视器 ,简单实现
    private class CustomTransferListener implements
            FTPDataTransferListener {
        private String optType;

        private boolean isCompleted = false;

        public CustomTransferListener() {
            super();
        }

        public CustomTransferListener(String optType) {
            this.optType = optType;
        }

        @Override
        public void started() {
            logger.debug(optType + ": FTP Starting...");
        }

        @Override
        public void transferred(int length) {
            logger.debug(optType + ":Transferring..." + length);
        }

        @Override
        public void completed() {
            isCompleted = true;
            logger.debug(optType + ":Complete!");
        }

        @Override
        public void aborted() {
            logger.warn(optType + ":Abort!");
        }

        @Override
        public void failed() {
            logger.warn(optType + ":Failed!");
        }
    }

    /**
     * 创建FTPClient对象
     *
     * @param serverIP
     * @param serverPort
     * @param ftpUser
     * @param ftpPwd
     * @return
     */
    private FTPClient getFtpClient(String serverIP, int serverPort, String ftpUser, String ftpPwd) {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            FTPConnector connector = new DirectConnector();
            connector.setCloseTimeout(30);//30秒钟关闭超时
            connector.setConnectionTimeout(30);//30秒连接超时
            connector.setReadTimeout(30);//30秒连接超时
            ftpClient.setConnector(connector);
            ftpClient.connect(serverIP, serverPort);
            ftpClient.login(ftpUser, ftpPwd);
            ftpClient.setPassive(true);
            ftpClient.setType(FTPClient.TYPE_BINARY);
            return ftpClient;
        } catch (Exception e) {
            logger.error("ftp connection error", e);
        }
        return ftpClient;
    }
}
