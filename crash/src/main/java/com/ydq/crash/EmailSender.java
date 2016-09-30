/*
 *
 * @author yandeqing
 * @created 2016.6.3
 * @email 18612205027@163.com
 * @version $version
 *
 */

package com.ydq.crash;

import android.util.Log;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;


/**
 * 邮件发送工具类
 * 20160318 yandeqing 修订 修复发送日志版本号为空的情况
 *
 * @author yandeqing
 */
public class EmailSender {
    private static final java.lang.String TAG = "EmailSender";
    private Properties properties;
    private Session session;
    private Message message;
    private MimeMultipart multipart;

    private String host;
    private String post;
    private String from;
    private String account;
    private String pwd;
    private List<String> receiver;
    //以上是必须参数


    private EmailSender() {
        this.host = EConfig.host;
        this.post = EConfig.post;
        this.from = EConfig.from;
        this.account = EConfig.account;
        this.pwd = EConfig.pwd;
        initProperties(host, post);
    }

    public static class Builder {
        private EmailSender emailSender;

        public Builder() {
            emailSender = new EmailSender();
        }

        public Builder from(String from) {
            emailSender.from = from;
            return this;
        }

        public Builder account(String account) {
            emailSender.account = account;
            return this;
        }

        public Builder pwd(String pwd) {
            emailSender.pwd = pwd;
            return this;
        }

        public Builder emailServerHostPost(String host, String post) {
            emailSender.host = host;
            emailSender.post = post;
            return this;
        }

        public Builder setReceiver(List<String> receiver) {
            emailSender.setReceiver(receiver);

            return this;
        }

        public EmailSender build() {
            return emailSender;
        }
    }

    /**
     * 初始化参数
     *
     * @param host
     * @param post
     */
    private void initProperties(String host, String post) {
        Log.i(TAG, "【EmailSender.send()】【host=" + host + ",post=" + post + "】");
        this.properties = new Properties();
        // 地址
        this.properties.put("mail.smtp.host", host);
        // 端口号
        this.properties.put("mail.smtp.post", post);
        // 是否验证
        this.properties.put("mail.smtp.auth", true);
        this.session = Session.getInstance(properties);
        this.message = new MimeMessage(session);
        this.multipart = new MimeMultipart("mixed");
    }

    /**
     * 设置收件人
     *
     * @param receiver
     * @throws MessagingException
     */
    public void setReceiver(List<String> receiver) {
        try {
            this.receiver = receiver;
            int size = receiver.size();
            Address[] address = new InternetAddress[size];
            for (int i = 0; i < size; i++) {
                address[i] = new InternetAddress(receiver.get(i));
            }
            this.message.setRecipients(Message.RecipientType.TO, address);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public void setReceiver(String receiver) throws MessagingException {
        Address address = new InternetAddress(receiver);
        this.message.setRecipient(Message.RecipientType.TO, address);
    }

    /**
     * 设置邮件
     *
     * @param title   标题
     * @param content 内容
     * @throws AddressException
     * @throws MessagingException
     */
    public void setMessage(String title, String content) throws MessagingException {
        this.message.setFrom(new InternetAddress(from));//发件人
        this.message.setSubject(title);
        // 纯文本的话用setText()就行，不过有附件就显示不出来内容了
        MimeBodyPart textBody = new MimeBodyPart();
        textBody.setContent(content, "text/html; charset=utf-8");
        this.multipart.addBodyPart(textBody);
    }

    /**
     * 添加附件
     *
     * @param attachmentPath 文件路径
     * @throws MessagingException
     */
    public void addAttachment(String attachmentPath) throws MessagingException {
        FileDataSource fileDataSource = new FileDataSource(new File(attachmentPath));
        DataHandler dataHandler = new DataHandler(fileDataSource);
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDataHandler(dataHandler);
        mimeBodyPart.setFileName(fileDataSource.getName());
        this.multipart.addBodyPart(mimeBodyPart);

    }

    /**
     * @param filePath
     * @throws MessagingException
     */
    public void addAttachments(List<String> filePath) throws MessagingException {
        // 发送邮件
        // 添加录音文件附件
        for (int i = 0; i < filePath.size(); i++) {
            // sender.addAttachment(filePath.get(i));
            MimeBodyPart html = new MimeBodyPart();
            FileDataSource dataSource = new FileDataSource(filePath.get(i));
            html.setDataHandler(new DataHandler(dataSource));
            try {
                html.setFileName(MimeUtility.encodeText(filePath.get(i)));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            multipart.addBodyPart(html);
        }
    }

    /**
     * 发送邮件
     */
    public void send() throws MessagingException {
        // 发送时间
        this.message.setSentDate(new Date());
        // 发送的内容，文本和附件
        this.message.setContent(this.multipart);
        this.message.saveChanges();
        // 创建邮件发送对象，并指定其使用SMTP协议发送邮件
        Transport transport = session.getTransport("smtp");
        // 登录邮箱
        Log.i(TAG, "【EmailSender.send()】【account=" + account + ",host=" + host + "】");
        Log.i(TAG, "【EmailSender.send()】【pwd=" + pwd + "】");
        transport.connect(host, account, pwd);
        // 发送邮件
        transport.sendMessage(message, message.getAllRecipients());
        // 关闭连接
        transport.close();
    }
}
