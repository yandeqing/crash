/*
 *
 * @author yandeqing
 * @created 2016.6.3
 * @email 18612205027@163.com
 * @version $version
 *
 */

package com.ydq.crash;

import android.content.Context;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private static final String TAG_NAME = "RPLfexaFoGXBaxiyZK9kCw==";
    private static final SimpleDateFormat DATE_FORMAT;
    private static String from;
    private static String name;
    static {
        from = "1292234542@qq.com";
        name = DESUtil.decryptDES(TAG_NAME);
        DATE_FORMAT = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    }

    public Properties properties;
    public Session session;
    public Message message;
    public MimeMultipart multipart;

    public EmailSender() {
        super();
        this.properties = new Properties();
    }

    /**
     * 将错误日志信息发送email
     * <p/>
     * String 主题名称
     *
     * @param attachmentPath String 附件的绝地地址路径
     *                       String email的内容信息
     *                       String 附件的名称
     * @throws MessagingException
     */
    public static void sendClientErrorLogEmail(String attachmentPath,
                                               Context context) throws MessagingException {
        String errContent = getStrFromFile(attachmentPath);
        sendTextByEmail(context, errContent, attachmentPath);
    }


    public static void sendTextByEmail(Context context, String errContent, String... attachmentPaths) throws MessagingException {
        EmailSender sender = new EmailSender();
        // 设置服务器地址和端口，网上搜的到
        sender.setProperties("smtp.qq.com", "25");
        // 分别设置发件人，邮件标题和文本内容
        StringBuilder title = new StringBuilder();
        title.append(context.getString(R.string.app_name));
        String versionName = SysInfoUtil.getVersionName(context);
        title.append(versionName);
        title.append("【机型:").append(Build.MODEL).append("】");
        title.append("【厂商:").append(Build.PRODUCT).append("】");
        title.append("【日期:").append(DATE_FORMAT.format(new Date())).append("】");

        StringBuilder content = new StringBuilder();
        content.append(errContent);
        content.append("<DIV><FONT color=#ff0000 size=3><STRONG>于");
        content.append(DATE_FORMAT.format(new Date(System.currentTimeMillis())));
        content.append(":</STRONG></FONT></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=3></FONT></STRONG>&nbsp;</DIV>");
        content.append("<DIV><FONT color=#ff0000 size=5 face=宋体><STRONG>&nbsp;&nbsp;&nbsp;&nbsp; APP运行期间发送异常错误,请各位同事查看处理！</STRONG></FONT></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=5 face=宋体></FONT></STRONG>&nbsp;</DIV>");
        content.append("<DIV><STRONG><FONT size=2>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</FONT></STRONG></DIV>");
        content.append("<DIV><STRONG><FONT size=2>发生异常错误的设备信息如下：</FONT></STRONG></DIV>");
        content.append("<DIV><STRONG><FONT size=2></FONT></STRONG>&nbsp;</DIV>");
        content.append("<DIV><STRONG><FONT size=2></FONT></STRONG>&nbsp;</DIV>");
        content.append(getDeviceInfo());
        content.append("<DIV><STRONG><FONT size=2></FONT></STRONG>&nbsp;</DIV>");
        content.append("<DIV><STRONG><FONT size=2></FONT></STRONG>&nbsp;</DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2></FONT></STRONG>&nbsp;</DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2>(该邮件来自"
                + context.getString(R.string.app_name)
                + "手机客户端)</FONT></STRONG></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2></FONT></STRONG>&nbsp;</DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2></FONT></STRONG>&nbsp;</DIV>");
        sender.setMessage(from, title.toString(),
                content.toString());
        // 设置收件人
        List<String> emails = new ArrayList<>();
        emails.add(from);
        String[] toEmails = emails.toArray(new String[emails.size()]);
        sender.setReceiver(toEmails);
        if (attachmentPaths != null && attachmentPaths.length > 0) {//附带附件，避免文件过长无法读取的情况
            // 添加附件
            String attachmentPath = attachmentPaths[0];
            if (attachmentPath != null) {
                sender.addAttachment(attachmentPath);
            }
        }
        // 发送邮件
        sender.sendEmail("smtp.qq.com", from,
                name);
    }

    public static String getStrFromFile(final String localUrl) {
        String str = "sdcard路径" + localUrl;
        File myFile = new File(localUrl);
        if (!myFile.exists()) {
            return null;
        }
        BufferedReader in = null;
        StringBuffer buffer = null;
        try {
            in = new BufferedReader(new FileReader(myFile));
            buffer = new StringBuffer("<br/>日志sdcard路径" + localUrl + "<br/>");
            while ((str = in.readLine()) != null) {
                if (str != null && str.contains("Exception")) {
                    buffer.append("<font color=red size=5 face=宋体><strong>"
                            + str + "</strong></font><br/>");
                } else {
                    buffer.append(str + "<br/>");
                }
            }
            if (in != null) {
                in.close();
            }
        } catch (Exception e) {
            e.getStackTrace();
            buffer.append("<font color=red size=5 face=宋体><strong>日志文件太长无法发送 OutOfMemoryError </strong></font><br/>\"");
        } catch (OutOfMemoryError e) {
            e.getStackTrace();
            buffer.append("<font color=red size=5 face=宋体><strong>日志文件太长无法发送 OutOfMemoryError </strong></font><br/>\"");
        }
        String string = buffer.toString();
        return string;
    }

    /**
     * 发送 反馈信息
     *
     * @param context
     * @throws MessagingException
     */
    public static void sendClientFeedBackEmail(String feedback, String contact, Context context)
            throws MessagingException {
        EmailSender sender = new EmailSender();
        // 设置服务器地址和端口，网上搜的到
        sender.setProperties("smtp.qq.com", "25");
        // 分别设置发件人，邮件标题和文本内容
        StringBuilder title = new StringBuilder();
        title.append("【意见反馈】");
        String versionName = SysInfoUtil.getVersionName(context);
        title.append("【版本:").append(versionName).append("】");
        title.append("【机型:").append(Build.MODEL).append("】");
        title.append("【厂商:").append(Build.PRODUCT).append("】");
        title.append("【日期:").append(DATE_FORMAT.format(new Date())).append("】");

        StringBuilder content = new StringBuilder();
        content.append("<DIV><FONT color=#ff0000 size=3><STRONG>于");
        content.append(DATE_FORMAT.format(new Date(System.currentTimeMillis())));
        content.append(":</STRONG></FONT></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=3></FONT></STRONG>&nbsp; </DIV>");
        content.append("<DIV><FONT color=#ff0000 size=5 face=宋体><STRONG>&nbsp;&nbsp;&nbsp;&nbsp; 收到微录音用户提交的意见反馈，请相关同事处理！ </STRONG></FONT></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=5 face=宋体></FONT></STRONG>&nbsp;&nbsp;&nbsp; ");
        content.append("<DIV><STRONG><FONT size=2>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ </FONT></STRONG></DIV>");
        content.append("<DIV><FONT color=#000000 size=5 face=宋体><STRONG>意见反馈信息如下：</STRONG></FONT></DIV>");
        content.append("<DIV><FONT color=#ff0000 size=4 face=宋体><STRONG></STRONG></FONT>&nbsp;</DIV>");
        content.append("<DIV><FONT color=#ff0000 size=5 face=宋体>【反馈内容】：");
        content.append(feedback);
        content.append("</FONT></DIV>");
        content.append("<DIV><FONT color=#ff0000 size=5 face=宋体></FONT>&nbsp;</DIV>");
        content.append("<DIV><FONT color=#ff0000 size=5 face=宋体>【联系方式】：");
        content.append(contact);
        content.append("</FONT></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=4 face=宋体></FONT></STRONG>&nbsp;</DIV></DIV>");
        content.append("<DIV><STRONG><FONT size=2>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ </FONT></STRONG></DIV>");
        content.append("<DIV><STRONG><FONT size=2>提交意见反馈的设备信息如下： </FONT></STRONG></DIV>");
        content.append("<DIV><STRONG><FONT size=2></FONT></STRONG>&nbsp; </DIV>");
        content.append(getDeviceInfo());
        content.append("<DIV><FONT color=#0000ff size=2 face=宋体></FONT>&nbsp;</DIV>");
        content.append("<DIV><STRONG><FONT size=2>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ </FONT></STRONG></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2></FONT></STRONG>&nbsp; </DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2>(该邮件来自高保真手机客户端) </FONT></STRONG></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2></FONT></STRONG>&nbsp; </DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2></FONT></STRONG>&nbsp; </DIV>");

        sender.setMessage("1292234542@qq.com", title.toString(),
                content.toString());
        // 设置收件人
//		String[] toEmails = { "android88@qq.com", "gunpoder@163.com",
//				"546218945@qq.com", "135213068@qq.com", "1371066685@qq.com" };
        String[] toEmails = {"546218945@qq.com"};
        sender.setReceiver(toEmails);
        // 发送邮件
        // 密码做加密处理
        sender.sendEmail("smtp.163.com", "", "");
    }

    /**
     * 通过又邮件发送微录音文件
     *
     * @throws MessagingException
     */
    public static void sendEmail(String[] toEmailsAddress, List<String> filePath, Context context)
            throws MessagingException {
        EmailSender sender = new EmailSender();
        // 设置服务器地址和端口，网上搜的到
        sender.setProperties("smtp.163.com", "25");
        // 分别设置发件人，邮件标题和文本内容
        StringBuilder title = new StringBuilder();
        title.append("【邮件发送】");
        String versionName = SysInfoUtil.getVersionName(context);
        title.append("【版本:").append(versionName).append("】");
        title.append("【机型:").append(Build.MODEL).append("】");
        title.append("【厂商:").append(Build.PRODUCT).append("】");
        title.append("【日期:").append(DATE_FORMAT.format(new Date())).append("】");

        StringBuilder content = new StringBuilder();
        content.append("<DIV><FONT color=#ff0000 size=3><STRONG>于");
        content.append(DATE_FORMAT.format(new Date(System.currentTimeMillis())));
        content.append(":</STRONG></FONT></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=3></FONT></STRONG>&nbsp; </DIV>");
        content.append("<DIV><FONT color=#ff0000 size=5 face=宋体><STRONG>&nbsp;&nbsp;&nbsp;&nbsp; 收到微录音录音文件，请及时查看！ </STRONG></FONT></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=5 face=宋体></FONT></STRONG>&nbsp;&nbsp;&nbsp; ");
        content.append("<DIV><STRONG><FONT size=2>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ </FONT></STRONG></DIV>");
        // content.append("<DIV><FONT color=#000000 size=5 face=宋体><STRONG>意见反馈信息如下：</STRONG></FONT></DIV>");
        // content.append("<DIV><FONT color=#ff0000 size=4 face=宋体><STRONG></STRONG></FONT>&nbsp;</DIV>");
        // content.append("<DIV><FONT color=#ff0000 size=5 face=宋体>【反馈内容】：");
        // // content.append(feedback);
        // content.append("</FONT></DIV>");
        // content.append("<DIV><FONT color=#ff0000 size=5 face=宋体></FONT>&nbsp;</DIV>");
        // content.append("<DIV><FONT color=#ff0000 size=5 face=宋体>【联系方式】：");
        // // content.append(contact);
        // content.append("</FONT></DIV>");
        // content.append("<DIV><STRONG><FONT color=#ff0000 size=4 face=宋体></FONT></STRONG>&nbsp;</DIV></DIV>");
        // content.append("<DIV><STRONG><FONT size=2>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ </FONT></STRONG></DIV>");
        // content.append("<DIV><STRONG><FONT size=2>提交意见反馈的设备信息如下： </FONT></STRONG></DIV>");
        // content.append("<DIV><STRONG><FONT size=2></FONT></STRONG>&nbsp; </DIV>");
        // content.append(getDeviceInfo());
        content.append("<DIV><FONT color=#0000ff size=2 face=宋体></FONT>&nbsp;</DIV>");
        content.append("<DIV><STRONG><FONT size=2>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ </FONT></STRONG></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2></FONT></STRONG>&nbsp; </DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2>(该邮件来自微录音手机客户端) </FONT></STRONG></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2></FONT></STRONG>&nbsp; </DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2></FONT></STRONG>&nbsp; </DIV>");

        sender.setMessage("", title.toString(),
                content.toString());
        // 设置收件人
        sender.setReceiver(toEmailsAddress);
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
            sender.multipart.addBodyPart(html);
        }

        sender.sendEmail("smtp.163.com", "", "");
    }

    private static String getDeviceInfo() {
        final StringBuilder result = new StringBuilder();
        final Field[] fields_build = Build.class.getFields();
        // 设备信息
        for (final Field field : fields_build) {
            String name = field.getName();
            result.append("<DIV><FONT color=#0000ff size=2 face=宋体>");
            result.append(parseDeviceStr(name));
            try {
                result.append(field.get(null).toString());
            } catch (Exception e) {
                result.append("N/A 未知");
            }
            result.append("</FONT></DIV>");
        }
        // 版本信息
        final Field[] fields_version = Build.VERSION.class.getFields();
        for (final Field field : fields_version) {
            String name = field.getName();
            result.append("<DIV><FONT color=#0000ff size=2 face=宋体>");
            result.append(parseDeviceStr(name));
            try {
                result.append(field.get(null).toString());
            } catch (Exception e) {
                result.append("N/A 未知");
            }
            result.append("</FONT></DIV>");
        }
        return result.toString();
    }

    private static String parseDeviceStr(String name) {
        if (name.equalsIgnoreCase("IMEI")) { // 设备串号
            return "【设备串号】IMEI：";
        } else if (name.equalsIgnoreCase("BOARD")) { // 主板
            return "【主板】BOARD：";
        } else if (name.equalsIgnoreCase("BOOTLOADER")) { // 引导
            return "【引导】BOOTLOADER：";
        } else if (name.equalsIgnoreCase("BRAND")) { // Android系统定制商
            return "【Android系统定制商】BRAND：";
        } else if (name.equalsIgnoreCase("CPU_ABI")) { // CPU指令集
            return "【CPU指令集】CPU_ABI：";
        } else if (name.equalsIgnoreCase("CPU_ABI2")) { // CPU指令集
            return "【CPU指令集】CPU_ABI2：";
        } else if (name.equalsIgnoreCase("DEVICE")) { // 设备参数
            return "【设备参数】DEVICE：";
        } else if (name.equalsIgnoreCase("DISPLAY")) { // 显示屏参数
            return "【显示屏参数】DISPLAY：";
        } else if (name.equalsIgnoreCase("FINGERPRINT")) { // 硬件名称
            return "【硬件名称】FINGERPRINT：";
        } else if (name.equalsIgnoreCase("HARDWARE")) { // 硬件
            return "【硬件】HARDWARE：";
        } else if (name.equalsIgnoreCase("HOST")) { // 主机
            return "【主机】HOST：";
        } else if (name.equalsIgnoreCase("ID")) { // 修订版本列表
            return "【修订版本列表 】ID：";
        } else if (name.equalsIgnoreCase("MANUFACTURER")) { // 硬件制造商
            return "【硬件制造商】MANUFACTURER：";
        } else if (name.equalsIgnoreCase("MODEL")) { // 机型
            return "【机型】MODEL：";
        } else if (name.equalsIgnoreCase("PRODUCT")) { // 手机制造商
            return "【手机制造商】PRODUCT：";
        } else if (name.equalsIgnoreCase("RADIO")) { // 无线电通讯
            return "【无线电通讯】RADIO：";
        } else if (name.equalsIgnoreCase("SERIAL")) { // 序列
            return "【序列】SERIAL：";
        } else if (name.equalsIgnoreCase("TAGS")) { // 描述build的标签
            return "【描述build的标签 】TAGS：";
        } else if (name.equalsIgnoreCase("TIME")) { // 时间
            return "【时间】TIME：";
        } else if (name.equalsIgnoreCase("TYPE")) { // builder类型
            return "【builder类型 】TYPE：";
        } else if (name.equalsIgnoreCase("UNKNOWN")) { // 未知
            return "【未知】UNKNOWN：";
        } else if (name.equalsIgnoreCase("USER")) { // 用户
            return "【用户】USER：";
        } else if (name.equalsIgnoreCase("BASEBAND")) { // 基带
            return "【基带】BASEBAND：";
        } else if (name.equalsIgnoreCase("CODENAME")) { // 当前开发代号
            return "【当前开发代号】CODENAME：";
        } else if (name.equalsIgnoreCase("HW_VERSION")) { // 硬件版本
            return "【硬件版本】HW_VERSION：";
        } else if (name.equalsIgnoreCase("INCREMENTAL")) { // 源码控制版本号
            return "【源码控制版本号】INCREMENTAL：";
        } else if (name.equalsIgnoreCase("RELEASE")) { // 系统版本号
            return "【系统版本号 】RELEASE：";
        } else if (name.equalsIgnoreCase("SDK")) { // SDK版本号
            return "【SDK版本号】SDK：";
        } else if (name.equalsIgnoreCase("SDK_INT")) { // SDK_INT版本号
            return "【SDK_INT版本号】SDK_INT：";
        } else if (name.equalsIgnoreCase("SW_VERSION")) { // 软件版本
            return "【软件版本】SW_VERSION：";
        } else if (name.equalsIgnoreCase("AppVerName")) { // 客户端版本
            return "【客户端版本】AppVerName：";
        } else { // 未知
            return "【未知】" + name + "：";
        }
    }

    public void setProperties(String host, String post) {
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
    public void setReceiver(String[] receiver) throws MessagingException {
        Address[] address = new InternetAddress[receiver.length];
        for (int i = 0; i < receiver.length; i++) {
            address[i] = new InternetAddress(receiver[i]);
        }
        this.message.setRecipients(Message.RecipientType.TO, address);
    }

    /**
     * 设置邮件
     *
     * @param from    来源
     * @param title   标题
     * @param content 内容
     * @throws AddressException
     * @throws MessagingException
     */
    public void setMessage(String from, String title, String content)
            throws AddressException,

            MessagingException {
        this.message.setFrom(new InternetAddress(from));
        this.message.setSubject(title);
        // 纯文本的话用setText()就行，不过有附件就显示不出来内容了
        MimeBodyPart textBody = new MimeBodyPart();
        textBody.setContent(content, "text/html; charset=utf-8");
        this.multipart.addBodyPart(textBody);
    }

    /**
     * 添加附件
     *
     * @param filePath 文件路径
     * @throws MessagingException
     */
    public void addAttachment(String filePath) throws MessagingException {
        FileDataSource fileDataSource = new FileDataSource(new File(filePath));
        DataHandler dataHandler = new DataHandler(fileDataSource);
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDataHandler(dataHandler);
        mimeBodyPart.setFileName(fileDataSource.getName());
        this.multipart.addBodyPart(mimeBodyPart);
    }

    /**
     * 发送邮件
     *
     * @param host    地址
     * @param account 账户名
     * @param pwd     密码
     * @throws MessagingException
     */
    public void sendEmail(String host, String account, String pwd)
            throws MessagingException {
        // 发送时间
        this.message.setSentDate(new Date());
        // 发送的内容，文本和附件
        this.message.setContent(this.multipart);
        this.message.saveChanges();
        // 创建邮件发送对象，并指定其使用SMTP协议发送邮件
        Transport transport = session.getTransport("smtp");
        // 登录邮箱
        transport.connect(host, account, pwd);
        // 发送邮件
        transport.sendMessage(message, message.getAllRecipients());
        // 关闭连接
        transport.close();
    }
}
