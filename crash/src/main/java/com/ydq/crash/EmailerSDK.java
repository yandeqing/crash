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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;


/**
 * 邮件发送工具类
 * 20160318 yandeqing 修订 修复发送日志版本号为空的情况
 *
 * @author yandeqing
 */
public class EmailerSDK {

    private static List<String> receivers;

    public static void sendClientErrorLogEmail(Context context, String attachmentPath) {
        String errContent = FileUtil.getStrFromFile(attachmentPath);
        sendTextByEmail(context, errContent, attachmentPath);
    }

    public static void sendTextByEmail(Context context, String errContent, String... attachmentPaths) {
        EmailSender.Builder builder = new EmailSender.Builder();
        EmailSender sender = builder.build();
        if (receivers != null) {
            sender.setReceiver(receivers);
        }
        // 分别设置发件人，邮件标题和文本内容
        StringBuilder title = new StringBuilder();
        title.append(SysInfoUtil.getAppName(context));
        String versionName = SysInfoUtil.getVersionName(context);
        title.append(versionName);
        title.append("【机型:").append(Build.MODEL).append("】");
        title.append("【厂商:").append(Build.PRODUCT).append("】");
        title.append("【日期:").append(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date())).append("】");
        StringBuilder content = new StringBuilder();
        content.append(errContent);
        content.append("<DIV><FONT color=#ff0000 size=3><STRONG>于");
        content.append(new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date(System.currentTimeMillis())));
        content.append(":</STRONG></FONT></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=3></FONT></STRONG>&nbsp;</DIV>");
        content.append("<DIV><FONT color=#ff0000 size=5 face=宋体><STRONG>&nbsp;&nbsp;&nbsp;&nbsp; APP运行期间发送异常错误,请各位同事查看处理！</STRONG></FONT></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=5 face=宋体></FONT></STRONG>&nbsp;</DIV>");
        content.append("<DIV><STRONG><FONT size=2>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</FONT></STRONG></DIV>");
        content.append("<DIV><STRONG><FONT size=2>发生异常错误的设备信息如下：</FONT></STRONG></DIV>");
        content.append("<DIV><STRONG><FONT size=2></FONT></STRONG>&nbsp;</DIV>");
        content.append("<DIV><STRONG><FONT size=2></FONT></STRONG>&nbsp;</DIV>");
        content.append(EConfig.getDeviceInfo());
        content.append("<DIV><STRONG><FONT size=2></FONT></STRONG>&nbsp;</DIV>");
        content.append("<DIV><STRONG><FONT size=2></FONT></STRONG>&nbsp;</DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2></FONT></STRONG>&nbsp;</DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2>(该邮件来自"
                + SysInfoUtil.getAppName(context)
                + "手机客户端)</FONT></STRONG></DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2></FONT></STRONG>&nbsp;</DIV>");
        content.append("<DIV><STRONG><FONT color=#ff0000 size=2></FONT></STRONG>&nbsp;</DIV>");
        try {
            sender.setMessage(title.toString(), content.toString());
            // 设置收件人
            List<String> emails = new ArrayList<>();
            emails.add(EConfig.account);
            sender.setReceiver(emails);
            if (attachmentPaths != null && attachmentPaths.length > 0) {//附带附件，避免文件过长无法读取的情况
                // 添加附件
                String attachmentPath = attachmentPaths[0];
                if (attachmentPath != null) {
                    sender.addAttachment(attachmentPath);
                }
            }
            // 发送邮件
            sender.send();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public static void setReceivers(List<String> receivers) {
        EmailerSDK.receivers = receivers;
    }
}
