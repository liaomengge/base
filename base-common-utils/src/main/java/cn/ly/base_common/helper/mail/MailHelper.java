package cn.ly.base_common.helper.mail;

import cn.ly.base_common.helper.mail.body.InlineMailBody;
import cn.ly.base_common.utils.log4j2.MwLogger;
import cn.ly.base_common.utils.number.MwNumberUtil;
import cn.ly.base_common.utils.properties.MwPropertiesUtil;
import cn.ly.base_common.utils.thread.MwThreadPoolExecutorUtil;
import cn.ly.base_common.helper.mail.body.AttachmentMailBody;
import cn.ly.base_common.helper.mail.body.AttachmentMailBody.AttachmentMailFile;
import cn.ly.base_common.helper.mail.body.HtmlMailBody;
import cn.ly.base_common.helper.mail.body.TextMailBody;
import cn.ly.base_common.support.misc.Symbols;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 邮件发送工具类
 */
public class MailHelper implements InitializingBean {

    private static Logger logger = MwLogger.getInstance(MailHelper.class);

    private ThreadPoolExecutor mailThreadPool = MwThreadPoolExecutorUtil.buildCpuCoreThreadPool("mail",
            new LinkedBlockingQueue<>(32), new ThreadPoolExecutor.DiscardPolicy());

    private JavaMailSenderImpl mailSender;

    private String smtpHost;
    private int smtpPort;
    private String senderUserName;
    private String senderPassword;
    private String senderNickName;
    private String receiver;
    private String ccReceiver;
    private String bccReceiver;

    public MailHelper(Properties properties) {
        smtpHost = MwPropertiesUtil.getStringProperty(properties, "mail.smtp.host");
        smtpPort = MwNumberUtil.getIntValue(MwPropertiesUtil.getIntProperty(properties, "mail.smtp.port", 465));
        senderUserName = MwPropertiesUtil.getStringProperty(properties, "mail.sender.username");
        senderNickName = MwPropertiesUtil.getStringProperty(properties, "mail.sender.nickname");
        receiver = MwPropertiesUtil.getStringProperty(properties, "mail.to");
        ccReceiver = MwPropertiesUtil.getStringProperty(properties, "mail.cc");
        bccReceiver = MwPropertiesUtil.getStringProperty(properties, "mail.bcc");
    }

    public MailHelper(String smtpHost, int smtpPort, String senderUserName, String senderPassword,
                      String senderNickName, String receiver, String ccReceiver, String bccReceiver) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.senderUserName = senderUserName;
        this.senderPassword = senderPassword;
        this.senderNickName = senderNickName;
        this.receiver = receiver;
        this.ccReceiver = ccReceiver;
        this.bccReceiver = bccReceiver;
    }

    /**
     * 发送纯文件邮件
     *
     * @param subject  邮件标题
     * @param mailBody 邮件内容
     */
    public void sendTextMail(String subject, String mailBody) {
        sendEmail(subject, mailBody, senderNickName, receiver, ccReceiver, bccReceiver);
    }

    /**
     * 发送纯文件邮件
     *
     * @param subject  邮件标题
     * @param mailBody 邮件内容
     */
    public void sendTextMail(String subject, TextMailBody mailBody) {
        sendEmail(subject, mailBody, senderNickName, receiver, ccReceiver, bccReceiver);
    }

    /**
     * 发送html邮件
     *
     * @param subject  邮件标题
     * @param mailBody 邮件内容
     */
    public void sendHtmlMail(String subject, HtmlMailBody mailBody) {
        sendEmail(subject, mailBody, senderNickName, receiver, ccReceiver, bccReceiver);
    }

    /**
     * 发送内嵌邮件
     *
     * @param subject  邮件标题
     * @param mailBody 邮件内容
     */
    public void sendInlineMail(String subject, InlineMailBody mailBody) {
        sendEmail(subject, mailBody, senderNickName, receiver, ccReceiver, bccReceiver);
    }

    /**
     * 发送附件邮件
     *
     * @param subject  邮件标题
     * @param mailBody 邮件内容
     */
    public void sendAttachmentMail(String subject, AttachmentMailBody mailBody) {
        sendEmail(subject, mailBody, senderNickName, receiver, ccReceiver, bccReceiver);
    }

    /**
     * 发送纯文本邮件
     *
     * @param subject     邮件标题
     * @param mailBody    邮件内容
     * @param receiveUser 收件人地址
     */
    public void sendTextMail(String subject, String mailBody, String receiveUser) {
        sendEmail(subject, mailBody, null, receiveUser, null, null);
    }

    /**
     * 发送纯文本邮件
     *
     * @param subject        邮件标题
     * @param mailBody       邮件内容
     * @param receiveUser    收件人地址
     * @param ccReceiverUser 抄送地址
     */
    public void sendTextMail(String subject, String mailBody, String receiveUser, String ccReceiverUser) {
        sendEmail(subject, mailBody, null, receiveUser, ccReceiverUser, null);
    }

    /**
     * 发送纯文本邮件
     *
     * @param subject        邮件标题
     * @param mailBody       邮件内容
     * @param receiveUser    收件人地址
     * @param ccReceiverUser 抄送地址
     */
    public void sendTextMail(String subject, TextMailBody mailBody, String receiveUser, String ccReceiverUser) {
        sendEmail(subject, mailBody, null, receiveUser, ccReceiverUser, null);
    }

    /**
     * 发送html邮件
     *
     * @param subject        邮件标题
     * @param mailBody       邮件内容
     * @param receiveUser    收件人地址
     * @param ccReceiverUser 抄送地址
     */
    public void sendHtmlMail(String subject, HtmlMailBody mailBody, String receiveUser, String ccReceiverUser) {
        sendEmail(subject, mailBody, null, receiveUser, ccReceiverUser, null);
    }

    /**
     * 发送内嵌邮件
     *
     * @param subject        邮件标题
     * @param mailBody       邮件内容
     * @param receiveUser    收件人地址
     * @param ccReceiverUser 抄送地址
     */
    public void sendInlineMail(String subject, InlineMailBody mailBody, String receiveUser, String ccReceiverUser) {
        sendEmail(subject, mailBody, null, receiveUser, ccReceiverUser, null);
    }

    /**
     * 发送附件邮件
     *
     * @param subject        邮件标题
     * @param mailBody       邮件内容
     * @param receiveUser    收件人地址
     * @param ccReceiverUser 抄送地址
     */
    public void sendAttachmentMail(String subject, AttachmentMailBody mailBody, String receiveUser,
                                   String ccReceiverUser) {
        sendEmail(subject, mailBody, null, receiveUser, ccReceiverUser, null);
    }

    /**
     * 发送邮件
     *
     * @param subject
     * @param mailBody
     * @param senderNickName
     * @param receiveUser
     * @param ccReceiveUser
     * @param bccReceiveUser
     */
    private void sendEmail(String subject, String mailBody, String senderNickName,
                           String receiveUser, String ccReceiveUser, String bccReceiveUser) {
        if (StringUtils.isBlank(receiveUser)) {
            return;
        }

        mailThreadPool.execute(() -> sendTo(subject, mailBody, senderNickName, receiveUser, ccReceiveUser,
                bccReceiveUser));
    }

    /**
     * 发送普通邮件
     *
     * @param subject
     * @param mailBody
     * @param senderNickName
     * @param receiveUser
     * @param ccReceiveUser
     * @param bccReceiveUser
     */
    private void sendEmail(String subject, TextMailBody mailBody, String senderNickName,
                           String receiveUser, String ccReceiveUser, String bccReceiveUser) {
        if (StringUtils.isBlank(receiveUser)) {
            return;
        }

        mailThreadPool.execute(() -> sendTo(subject, mailBody, senderNickName, receiveUser, ccReceiveUser,
                bccReceiveUser));
    }

    /**
     * 发送html邮件
     *
     * @param subject
     * @param mailBody
     * @param senderNickName
     * @param receiveUser
     * @param ccReceiveUser
     * @param bccReceiveUser
     */
    private void sendEmail(String subject, HtmlMailBody mailBody, String senderNickName,
                           String receiveUser, String ccReceiveUser, String bccReceiveUser) {
        if (StringUtils.isBlank(receiveUser)) {
            return;
        }

        mailThreadPool.execute(() -> sendTo(subject, mailBody, senderNickName, receiveUser, ccReceiveUser,
                bccReceiveUser));
    }

    /**
     * 发送内嵌邮件
     *
     * @param subject
     * @param mailBody
     * @param senderNickName
     * @param receiveUser
     * @param ccReceiveUser
     * @param bccReceiveUser
     */
    private void sendEmail(String subject, InlineMailBody mailBody, String senderNickName,
                           String receiveUser, String ccReceiveUser, String bccReceiveUser) {
        if (StringUtils.isBlank(receiveUser)) {
            return;
        }

        mailThreadPool.execute(() -> sendTo(subject, mailBody, senderNickName, receiveUser, ccReceiveUser,
                bccReceiveUser));
    }

    /**
     * 发送附件邮件
     *
     * @param subject
     * @param mailBody
     * @param senderNickName
     * @param receiveUser
     * @param ccReceiveUser
     * @param bccReceiveUser
     */
    private void sendEmail(String subject, AttachmentMailBody mailBody, String senderNickName,
                           String receiveUser, String ccReceiveUser, String bccReceiveUser) {
        if (StringUtils.isBlank(receiveUser)) {
            return;
        }

        mailThreadPool.execute(() -> sendTo(subject, mailBody, senderNickName, receiveUser, ccReceiveUser,
                bccReceiveUser));
    }

    private void sendTo(String subject, String mailBody, String senderNickName,
                        String receiveUser, String ccReceiveUser, String bccReceiveUser) {
        int sendSuccessCount = 0;

        String[] toArr = StringUtils.split(receiveUser, Symbols.SEMICOLON);
        for (String to : toArr) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            String senderFrom = senderUserName;
            if (StringUtils.isNotBlank(senderNickName)) {
                senderFrom = senderNickName + " <" + senderUserName + ">";
            }
            mailMessage.setFrom(senderFrom);
            mailMessage.setTo(to);

            if (sendSuccessCount == 0) {
                if (StringUtils.isNotBlank(ccReceiveUser)) {
                    mailMessage.setCc(StringUtils.split(ccReceiveUser, Symbols.SEMICOLON));
                }
                if (StringUtils.isNotBlank(bccReceiveUser)) {
                    mailMessage.setBcc(StringUtils.split(bccReceiveUser, Symbols.SEMICOLON));
                }
            }

            mailMessage.setSubject(subject);
            mailMessage.setText(mailBody);

            try {
                mailSender.send(mailMessage);
                sendSuccessCount++;
                logger.info(senderUserName + " 向 " + to + " 发送邮件成功!");
            } catch (Exception e) {
                logger.error("sendEmail to [" + to + "]失败!", e);
            }
        }
    }

    private void sendTo(String subject, TextMailBody mailBody, String senderNickName,
                        String receiveUser, String ccReceiveUser, String bccReceiveUser) {
        int sendSuccessCount = 0;

        String[] toArr = StringUtils.split(receiveUser, Symbols.SEMICOLON);
        for (String to : toArr) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            String senderFrom = senderUserName;
            if (StringUtils.isNotBlank(senderNickName)) {
                senderFrom = senderNickName + " <" + senderUserName + ">";
            }
            mailMessage.setFrom(senderFrom);
            mailMessage.setTo(to);

            if (sendSuccessCount == 0) {
                if (StringUtils.isNotBlank(ccReceiveUser)) {
                    mailMessage.setCc(StringUtils.split(ccReceiveUser, Symbols.SEMICOLON));
                }
                if (StringUtils.isNotBlank(bccReceiveUser)) {
                    mailMessage.setBcc(StringUtils.split(bccReceiveUser, Symbols.SEMICOLON));
                }
            }

            mailMessage.setSubject(subject);
            mailMessage.setText(mailBody.getText());

            try {
                mailSender.send(mailMessage);
                sendSuccessCount++;
                logger.info(senderUserName + " 向 " + to + " 发送邮件成功!");
            } catch (Exception e) {
                logger.error("sendEmail to [" + to + "]失败!", e);
            }
        }
    }

    private void sendTo(String subject, HtmlMailBody mailBody, String senderNickName,
                        String receiveUser, String ccReceiveUser, String bccReceiveUser) {
        int sendSuccessCount = 0;

        String[] toArr = StringUtils.split(receiveUser, Symbols.SEMICOLON);
        for (String to : toArr) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

                String senderFrom = senderUserName;
                if (StringUtils.isNotBlank(senderNickName)) {
                    senderFrom = senderNickName + " <" + senderUserName + ">";
                }
                mimeMessageHelper.setFrom(senderFrom);
                mimeMessageHelper.setTo(to);

                if (sendSuccessCount == 0) {
                    if (StringUtils.isNotBlank(ccReceiveUser)) {
                        mimeMessageHelper.setCc(StringUtils.split(ccReceiveUser, Symbols.SEMICOLON));
                    }
                    if (StringUtils.isNotBlank(bccReceiveUser)) {
                        mimeMessageHelper.setBcc(StringUtils.split(bccReceiveUser, Symbols.SEMICOLON));
                    }
                }

                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(mailBody.getText(), true);

                mailSender.send(mimeMessage);
                sendSuccessCount++;
                logger.info(senderUserName + " 向 " + to + " 发送邮件成功!");
            } catch (Exception e) {
                logger.error("sendEmail to [" + to + "]失败!", e);
            }
        }
    }

    private void sendTo(String subject, InlineMailBody mailBody, String senderNickName,
                        String receiveUser, String ccReceiveUser, String bccReceiveUser) {
        int sendSuccessCount = 0;

        String[] toArr = StringUtils.split(receiveUser, Symbols.SEMICOLON);
        for (String to : toArr) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

                String senderFrom = senderUserName;
                if (StringUtils.isNotBlank(senderNickName)) {
                    senderFrom = senderNickName + " <" + senderUserName + ">";
                }
                mimeMessageHelper.setFrom(senderFrom);
                mimeMessageHelper.setTo(to);

                if (sendSuccessCount == 0) {
                    if (StringUtils.isNotBlank(ccReceiveUser)) {
                        mimeMessageHelper.setCc(StringUtils.split(ccReceiveUser, Symbols.SEMICOLON));
                    }
                    if (StringUtils.isNotBlank(bccReceiveUser)) {
                        mimeMessageHelper.setBcc(StringUtils.split(bccReceiveUser, Symbols.SEMICOLON));
                    }
                }

                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(mailBody.getText(), true);

                List<InlineMailBody.InlineMailFile> inlineMailFiles = mailBody.getInlineMailFiles();
                if (CollectionUtils.isNotEmpty(inlineMailFiles)) {
                    for (InlineMailBody.InlineMailFile inlineMailFile : inlineMailFiles) {
                        mimeMessageHelper.addInline(inlineMailFile.getContentId(), inlineMailFile.getFile());
                    }
                }

                mailSender.send(mimeMessage);
                sendSuccessCount++;
                logger.info(senderUserName + " 向 " + to + " 发送邮件成功!");
            } catch (Exception e) {
                logger.error("sendEmail to [" + to + "]失败!", e);
            }
        }
    }

    private void sendTo(String subject, AttachmentMailBody mailBody, String senderNickName,
                        String receiveUser, String ccReceiveUser, String bccReceiveUser) {
        int sendSuccessCount = 0;

        String[] toArr = StringUtils.split(receiveUser, Symbols.SEMICOLON);
        for (String to : toArr) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

                String senderFrom = senderUserName;
                if (StringUtils.isNotBlank(senderNickName)) {
                    senderFrom = senderNickName + " <" + senderUserName + ">";
                }
                mimeMessageHelper.setFrom(senderFrom);
                mimeMessageHelper.setTo(to);

                if (sendSuccessCount == 0) {
                    if (StringUtils.isNotBlank(ccReceiveUser)) {
                        mimeMessageHelper.setCc(StringUtils.split(ccReceiveUser, Symbols.SEMICOLON));
                    }
                    if (StringUtils.isNotBlank(bccReceiveUser)) {
                        mimeMessageHelper.setBcc(StringUtils.split(bccReceiveUser, Symbols.SEMICOLON));
                    }
                }

                mimeMessageHelper.setSubject(subject);
                mimeMessageHelper.setText(mailBody.getText());

                List<AttachmentMailFile> attachmentMailFiles = mailBody.getAttachmentMailFiles();
                if (CollectionUtils.isNotEmpty(attachmentMailFiles)) {
                    for (AttachmentMailFile attachmentMailFile : attachmentMailFiles) {
                        mimeMessageHelper.addAttachment(attachmentMailFile.getAttachmentFilename(),
                                attachmentMailFile.getFile());
                    }
                }

                mailSender.send(mimeMessage);
                sendSuccessCount++;
                logger.info(senderUserName + " 向 " + to + " 发送邮件成功!");
            } catch (Exception e) {
                logger.error("sendEmail to [" + to + "]失败!", e);
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(senderUserName);
        mailSender.setPassword(senderPassword);
        mailSender.setHost(smtpHost);
        mailSender.setPort(smtpPort);

        mailSender.setProtocol("smtp");
        mailSender.setDefaultEncoding("utf-8");

        Properties mailProperties = new Properties();
        mailProperties.put("mail.smtp.auth", "true");
        mailProperties.put("mail.smtp.ssl.enable", true);
        mailProperties.put("mail.smtp.starttls.enable", true);

        mailProperties.put("mail.smtp.timeout", "5000");
        mailProperties.put("mail.smtp.port", "465");

        mailProperties.put("mail.smtp.socketFactory.port", "465");
        mailProperties.put("mail.smtp.socketFactory.fallback", "false");
        mailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        mailSender.setJavaMailProperties(mailProperties);
    }

}
